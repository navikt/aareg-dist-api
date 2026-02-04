package no.nav.aareg.dist.api.wiremock.aareg.mottak;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.extension.Extension;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import no.nav.aareg.dist.api.domain.mottak.api.v1.Arbeidsforhold;
import no.nav.aareg.dist.api.testdata.ArbeidsforholdDistMottakTestdataBuilder;
import no.nav.aareg.dist.api.wiremock.WireMockStub;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class AaregDistMottakStub implements WireMockStub {

    private final AaregDistMottakResponseTransformer responseTransformer;
    private final ArbeidsforholdDistMottakTestdataBuilder testDataBuilder;

    private List<Arbeidsforhold> arbeidsforholdListe = new ArrayList<>();

    StubMapping getStub;
    StubMapping postStub;

    public AaregDistMottakStub() {
        responseTransformer = new AaregDistMottakResponseTransformer(this);
        testDataBuilder = new ArbeidsforholdDistMottakTestdataBuilder();
    }

    @Override
    public Extension getWireMockExtension() {
        return responseTransformer;
    }

    @Override
    public void registerResponseMappingBeforeAll() {
        setupPostResponse();
        setupGetResponse();
    }

    @Override
    public void registerResponseMappingBeforeEach() {
        registerResponseMappingBeforeAll();
    }

    public void setupForFailOnGet() {
        WireMock.removeStub(getStub);
        getStub = stubFor(get(urlPathMatching("/aareg-dist-mottak/(.*)"))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withStatus(500)
                ));
    }

    public void setupForFailOnPost() {
        WireMock.removeStub(postStub);
        postStub = stubFor(post(urlPathMatching("/aareg-dist-mottak/(.*)"))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withStatus(500)
                ));
    }

    public void setupGetResponse() {
        getStub = stubFor(get(urlPathMatching("/aareg-dist-mottak/(.*)"))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withTransformers(responseTransformer.getName())));
    }

    public void setupPostResponse() {
        postStub = stubFor(post(urlPathMatching("/aareg-dist-mottak/(.*)"))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withTransformers(responseTransformer.getName())));
    }

    public void setResponseToDefaultArbeidsforhold() {
        arbeidsforholdListe = List.of(testDataBuilder.defaultArbeidsforhold().build());
    }

    public void setArbeidsforholdResponse(List<Arbeidsforhold> arbeidsforholdListe) {
        this.arbeidsforholdListe = arbeidsforholdListe;
    }

    public List<Arbeidsforhold> getArbeidsforholdListe() {
        return arbeidsforholdListe;
    }
}
