package no.nav.aareg.dist.api.consumer.aareg.mottak;

import lombok.RequiredArgsConstructor;
import no.nav.aareg.dist.api.consumer.aareg.mottak.dto.HentArbeidsforholdRequestDTO;
import no.nav.aareg.dist.api.consumer.aareg.mottak.dto.HentArbeidsforholdV1ResponseDTO;
import no.nav.aareg.dist.api.consumer.texas.TexasConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
@RequiredArgsConstructor
public class AaregDistMottakConsumer {

    private final RestClient aaregDistMottakRestClient;
    private final TexasConsumer texasConsumer;

    @Value("${app.scope.aareg.dist.mottak}")
    private String aaregDistMottakScope;

    public HentArbeidsforholdV1ResponseDTO hentArbeidsforhold(HentArbeidsforholdRequestDTO request) {
        return aaregDistMottakRestClient.post()
                .uri("/v1/arbeidsforhold/hent-arbeidsforhold")
                .headers(httpHeaders -> {
                    httpHeaders.setContentType(APPLICATION_JSON);
                    httpHeaders.setBearerAuth(texasConsumer.hentEntraToken(aaregDistMottakScope));
                })
                .body(request)
                .retrieve()
                .body(HentArbeidsforholdV1ResponseDTO.class);
    }
}
