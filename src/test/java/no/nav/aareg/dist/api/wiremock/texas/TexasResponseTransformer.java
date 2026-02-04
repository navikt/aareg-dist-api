package no.nav.aareg.dist.api.wiremock.texas;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformerV2;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import lombok.RequiredArgsConstructor;
import no.nav.aareg.dist.api.consumer.texas.dto.TexasResponse;
import tools.jackson.databind.json.JsonMapper;

@RequiredArgsConstructor
public class TexasResponseTransformer implements ResponseDefinitionTransformerV2 {

    private final TexasStub texasStub;
    private final JsonMapper jsonMapper;

    @Override
    public String getName() {
        return "texas-stub";
    }

    @Override
    public ResponseDefinition transform(ServeEvent serveEvent) {
        return ResponseDefinitionBuilder
                .like(serveEvent.getResponseDefinition())
                .withStatus(200)
                //.withHeader(HttpHeaders.CONNECTION, "close")
                .withBody(getResponseBody())
                .build();
    }

    @Override
    public boolean applyGlobally() {
        return false;
    }

    private String getResponseBody() {
        var tokenXResponse = new TexasResponse("token", "Bearer", 10000);
        return jsonMapper.writeValueAsString(tokenXResponse);
    }
}
