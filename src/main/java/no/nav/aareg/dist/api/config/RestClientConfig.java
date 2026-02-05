package no.nav.aareg.dist.api.config;

import lombok.extern.slf4j.Slf4j;
import no.nav.aareg.dist.api.consumer.exception.RetryableException;
import no.nav.aareg.dist.api.consumer.exception.UnrecoverableException;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
public class RestClientConfig {

    private static final Marker TEAM_LOGS_MARKER = MarkerFactory.getMarker("TEAM_LOGS");

    @Bean
    RestClient aaregDistMottakRestClient(@Value("${app.url.aareg.dist.mottak}") String baseUrl) {
        return restClientBuilder("aareg-dist-mottak")
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    RestClient aaregTilgangskontrollRestClient(@Value("${app.url.aareg.tilgangskontroll}") String baseUrl) {
        return restClientBuilder("aareg-tilgangskontroll")
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    RestClient texasRestClient() {
        return restClientBuilder("Texas").build();
    }

    public RestClient.Builder restClientBuilder(String tjeneste) {
        return RestClient.builder()
                .requestFactory(clientHttpRequestFactory())
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (clientRequest, clientResponse) -> {
                    var melding = String.format("Tjeneste [%s] returnerte client error med status %s og melding %s", tjeneste, clientResponse.getStatusCode(), new String(clientResponse.getBody().readAllBytes()));
                    log.error(TEAM_LOGS_MARKER, melding);
                    throw new UnrecoverableException(melding);
                })
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, (clientRequest, clientResponse) -> {
                    var melding = String.format("Tjeneste [%s] returnerte server error med status %s og melding %s", tjeneste, clientResponse.getStatusCode(), new String(clientResponse.getBody().readAllBytes()));
                    log.warn(TEAM_LOGS_MARKER, melding);
                    throw new RetryableException(melding);
                });
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
        var client = HttpClients.custom()
                .useSystemProperties()
                .setRetryStrategy(new DefaultHttpRequestRetryStrategy())
                .build();
        return new HttpComponentsClientHttpRequestFactory(client);
    }
}
