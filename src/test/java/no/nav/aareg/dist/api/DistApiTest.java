package no.nav.aareg.dist.api;

import no.nav.aareg.dist.api.wiremock.WireMockStubs;
import no.nav.aareg.dist.api.wiremock.aareg.mottak.AaregDistMottakStub;
import no.nav.aareg.dist.api.wiremock.aareg.tilgangskontroll.AaregTilgangskontrollStub;
import no.nav.aareg.dist.api.wiremock.maskinporten.MaskinportenStub;
import no.nav.aareg.dist.api.wiremock.texas.TexasStub;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@WireMockStubs({
        MaskinportenStub.class,
        TexasStub.class,
        AaregDistMottakStub.class,
        AaregTilgangskontrollStub.class
})
@SpringBootTest(
        classes = {AaregDistApi.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureRestTestClient
public class DistApiTest {

    protected MaskinportenStub maskinportenStub;
    protected AaregDistMottakStub aaregDistMottakStub;
    protected TexasStub texasStub;
    protected AaregTilgangskontrollStub aaregTilgangskontrollStub;
}
