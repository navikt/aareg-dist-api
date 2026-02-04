package no.nav.aareg.dist.api.wiremock.aareg.tilgangskontroll;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformerV2;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import no.nav.aareg.dist.api.consumer.aareg.tilgangskontroll.dto.Kontekst;
import no.nav.aareg.dist.api.consumer.aareg.tilgangskontroll.dto.Tilgang;
import no.nav.aareg.dist.api.consumer.aareg.tilgangskontroll.dto.TilgangsforespoerselRequest;
import no.nav.aareg.dist.api.consumer.aareg.tilgangskontroll.dto.TilgangsforespoerselResponse;
import org.springframework.http.HttpHeaders;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.Objects;

@RequiredArgsConstructor
public class AaregTilgangskontrollResponseTransformer implements ResponseDefinitionTransformerV2 {

    private final AaregTilgangskontrollStub aaregTilgangskontrollStub;

    @Override
    public String getName() {
        return "aareg-tilgangskontroll-stub";
    }

    @Override
    @SneakyThrows
    public ResponseDefinition transform(ServeEvent serveEvent) {
        var requestBody = new JsonMapper().readValue(serveEvent.getRequest().getBody(), TilgangsforespoerselRequest.class);
        TilgangsforespoerselResponse response;

        if (Objects.requireNonNull(requestBody.kontekst()) == Kontekst.SYSTEM_UTEN_TILGANG_TIL_ADRESSEBESKYTTELSE) {
            var adressebeskyttedePersoner = aaregTilgangskontrollStub.getAdressebeskyttedePersoner();
            var tilgangsliste = new ArrayList<Tilgang>();

            requestBody.forespurteArbeidstakere().forEach(forespurtArbeidstaker -> {
                var forespurtOpplysningspliktig = forespurtArbeidstaker.opplysningspliktigIdentifikator();
                var forespurtArbeidstakerIdentifikator = forespurtArbeidstaker.arbeidstakerIdentifikator();
                var harTilgang = adressebeskyttedePersoner.stream().noneMatch(p -> p.equals(forespurtOpplysningspliktig) || p.equals(forespurtArbeidstakerIdentifikator));

                if (harTilgang) {
                    tilgangsliste.add(
                            new Tilgang(
                                    forespurtOpplysningspliktig,
                                    forespurtArbeidstaker.arbeidsstedIdentifikator(),
                                    forespurtArbeidstaker.arbeidstakerIdentifikator(),
                                    null,
                                    true,
                                    adressebeskyttedePersoner.contains(forespurtOpplysningspliktig),
                                    adressebeskyttedePersoner.contains(forespurtArbeidstaker.arbeidstakerIdentifikator())
                            )
                    );
                } else {
                    tilgangsliste.add(
                            new Tilgang(
                                    forespurtOpplysningspliktig,
                                    forespurtArbeidstaker.arbeidsstedIdentifikator(),
                                    forespurtArbeidstaker.arbeidstakerIdentifikator(),
                                    "test",
                                    false,
                                    adressebeskyttedePersoner.contains(forespurtOpplysningspliktig),
                                    adressebeskyttedePersoner.contains(forespurtArbeidstaker.arbeidstakerIdentifikator())
                            )
                    );
                }
            });

            response = new TilgangsforespoerselResponse(tilgangsliste);
        } else {
            throw new RuntimeException("Ukjent kontekst");
        }
        var responseBody = new JsonMapper().writeValueAsString(response);

        return ResponseDefinitionBuilder
                .like(serveEvent.getResponseDefinition())
                .withStatus(200)
                .withHeader(HttpHeaders.CONNECTION, "close")
                .withBody(responseBody)
                .build();
    }

    @Override
    public boolean applyGlobally() {
        return false;
    }
}
