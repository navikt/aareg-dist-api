package no.nav.aareg.dist.api.graphql.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AaregDistFeilkode {

    /**
     * Felles feilkoder for api'er er intervallet [AA-000 - AA-099]
     */

    public static final AaregDistFeilkode INTERN_FEIL = new AaregDistFeilkode(
            "AA-000",
            "Det oppsto en feil under behandlingen av forespøselen",
            null
    );

    public static final AaregDistFeilkode UGYLDIG_SPOERRING = new AaregDistFeilkode(
            "AA-001",
            "Spørringen inneholder ugyldige variabler/verdier, eller er på et ugyldig format",
            null
    );

    public static final AaregDistFeilkode UGYLDIG_DATA = new AaregDistFeilkode(
            "AA-002",
            "Arbeidsforholdet er ikke komplett. Ta kontakt med brukerstøtte, og bruk korrelasjons-id som referanse",
            null
    );

    private final String kode;
    private final String beskrivelse;
    private final String detaljer;

    public Errormessage toErrorMessage() {
        return Errormessage.builder()
                .code(kode)
                .message(beskrivelse)
                .details(detaljer)
                .build();
    }
}
