package no.nav.aareg.dist.api.wiremock.aareg.tilgangskontroll;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.extension.Extension;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import lombok.Getter;
import lombok.Setter;
import no.nav.aareg.dist.api.wiremock.WireMockStub;
import org.springframework.http.HttpHeaders;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static no.nav.aareg.dist.api.testdata.ArbeidsforholdTestdataBuilder.ARBEIDSTAKER_FNR_MED_ADRESSESKJERMING;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class AaregTilgangskontrollStub implements WireMockStub {

    private final AaregTilgangskontrollResponseTransformer responseTransformer;

    @Getter
    @Setter
    private List<String> adressebeskyttedePersoner = List.of(ARBEIDSTAKER_FNR_MED_ADRESSESKJERMING);

    private List<StubMapping> stubMappings;

    public AaregTilgangskontrollStub() {
        this.responseTransformer = new AaregTilgangskontrollResponseTransformer(this);
    }

    @Override
    public Extension getWireMockExtension() {
        return responseTransformer;
    }

    @Override
    public void registerResponseMappingBeforeAll() {
        if (stubMappings != null) {
            stubMappings.forEach(WireMock::removeStub);
            stubMappings = null;
        }
        stubMappings = List.of(
                WireMock.stubFor(post(urlPathMatching("/aareg-tilgangskontroll/(.*)"))
                        .willReturn(aResponse()
                                .withStatus(OK.value())
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                .withHeader(HttpHeaders.CONNECTION, "close")
                                .withTransformers(responseTransformer.getName())))
        );
    }

    @Override
    public void registerResponseMappingBeforeEach() {
        registerResponseMappingBeforeAll();
    }

    private String getPort() {
        return System.getProperty("wiremock.server.port");
    }

    public void stubForNedetid() {
        if (stubMappings != null) {
            stubMappings.forEach(WireMock::removeStub);
            stubMappings = null;
        }
        stubMappings = List.of(
                WireMock.stubFor(post(urlPathMatching("/aareg-tilgangskontroll/(.*)"))
                        .willReturn(aResponse()
                                .withStatus(INTERNAL_SERVER_ERROR.value())
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                .withHeader(HttpHeaders.CONNECTION, "close")
                                .withBody("boom")))
        );
    }

    public void resetAdressebeskyttelser() {
        this.adressebeskyttedePersoner = List.of(ARBEIDSTAKER_FNR_MED_ADRESSESKJERMING);
    }
}
