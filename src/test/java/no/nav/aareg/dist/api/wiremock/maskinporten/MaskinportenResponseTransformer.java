package no.nav.aareg.dist.api.wiremock.maskinporten;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformerV2;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MaskinportenResponseTransformer implements ResponseDefinitionTransformerV2 {

    private final MaskinportenStub maskinportenStub;

    @Override
    public String getName() {
        return "maskinporten-stub";
    }

    @Override
    public ResponseDefinition transform(ServeEvent serveEvent) {
        var request = serveEvent.getRequest();
        var responseDef = serveEvent.getResponseDefinition();
        var requestedUrl = request.getUrl();

        String responseBody = getResponseBody(requestedUrl);

        if (responseBody != null) {
            return ResponseDefinitionBuilder
                    .like(responseDef)
                    .withStatus(200)
                    .withBody(responseBody)
                    .build();
        } else {
            return ResponseDefinitionBuilder
                    .like(responseDef)
                    .withStatus(404)
                    .build();
        }
    }

    @Override
    public boolean applyGlobally() {
        return false;
    }

    private String getResponseBody(String requestedUrl) {
        String responseBody = null;

        if ("/maskinporten/.well-known/openid-configuration".equals(requestedUrl)) {
            responseBody = maskinportenStub.getWellKnownOpenidConfiguration();
        } else if ("/maskinporten/jwks".equals(requestedUrl)) {
            responseBody = maskinportenStub.getJwks();
        } else if ("/maskinporten/.well-known/oauth-authorization-server".equals(requestedUrl)) {
            responseBody = maskinportenStub.getWellKnownOauthConfiguration();
        } else if ("/maskinporten/token".equals(requestedUrl)) {
            responseBody = maskinportenStub.getAccesstoken();
        }
        return responseBody;
    }
}
