package no.nav.aareg.dist.api.wiremock.aareg.mottak;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformerV2;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import no.nav.aareg.dist.api.consumer.aareg.mottak.dto.HentArbeidsforholdV1ResponseDTO;
import tools.jackson.databind.json.JsonMapper;

import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
public class AaregDistMottakResponseTransformer implements ResponseDefinitionTransformerV2 {

    private final AaregDistMottakStub aaregDistMottakStub;

    private final JsonMapper jsonMapper = new JsonMapper();

    @Override
    public String getName() {
        return "aareg-dist-mottak-stub";
    }

    @Override
    @SneakyThrows
    public ResponseDefinition transform(ServeEvent serveEvent) {
        String responseBody = getArbeidsforholdResponseBody();

        return ResponseDefinitionBuilder
                .like(serveEvent.getResponseDefinition())
                .withStatus(OK.value())
                .withBody(responseBody)
                .build();
    }

    @Override
    public boolean applyGlobally() {
        return false;
    }

    private String getArbeidsforholdResponseBody() {
        var response = HentArbeidsforholdV1ResponseDTO.builder()
                .arbeidsforhold(aaregDistMottakStub.getArbeidsforholdListe())
                .build();

        return jsonMapper.writeValueAsString(response);
    }
}
