package no.nav.aareg.dist.api.consumer.aareg.tilgangskontroll;

import lombok.RequiredArgsConstructor;
import no.nav.aareg.dist.api.consumer.aareg.tilgangskontroll.dto.TilgangsforespoerselRequest;
import no.nav.aareg.dist.api.consumer.aareg.tilgangskontroll.dto.TilgangsforespoerselResponse;
import no.nav.aareg.dist.api.consumer.texas.TexasConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class AaregTilgangskontrollConsumer {

    private final TexasConsumer texasConsumer;
    private final RestClient aaregTilgangskontrollRestClient;

    @Value("${app.scope.aareg.tilgangskontroll}")
    private String aaregTilgangskontrollScope;

    public TilgangsforespoerselResponse kontrollerTilganger(TilgangsforespoerselRequest tilgangsforespoerselRequest) {
        return aaregTilgangskontrollRestClient.post()
                .uri("/api/v1/kontroller")
                .body(tilgangsforespoerselRequest)
                .headers(httpHeaders -> {
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    httpHeaders.setBearerAuth(texasConsumer.hentEntraToken(aaregTilgangskontrollScope));
                })
                .retrieve()
                .body(TilgangsforespoerselResponse.class);
    }
}
