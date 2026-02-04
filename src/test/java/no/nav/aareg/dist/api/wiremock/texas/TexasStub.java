package no.nav.aareg.dist.api.wiremock.texas;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.extension.Extension;
import no.nav.aareg.dist.api.wiremock.WireMockStub;
import tools.jackson.databind.json.JsonMapper;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class TexasStub implements WireMockStub {

    private final TexasResponseTransformer responseTransformer;

    public TexasStub() {
        responseTransformer = new TexasResponseTransformer(this, new JsonMapper());
    }

    @Override
    public Extension getWireMockExtension() {
        return responseTransformer;
    }

    @Override
    public void registerResponseMappingBeforeAll() {
        WireMock.stubFor(get(urlPathMatching("/texas/exchange"))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withTransformers(responseTransformer.getName())));
        WireMock.stubFor(post(urlPathMatching("/texas/exchange"))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withTransformers(responseTransformer.getName())));
        WireMock.stubFor(post(urlPathMatching("/texas/token"))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withTransformers(responseTransformer.getName())));
    }

    @Override
    public void registerResponseMappingBeforeEach() {
        registerResponseMappingBeforeAll();
    }

    private String getPort() {
        return System.getProperty("wiremock.server.port");
    }
}
