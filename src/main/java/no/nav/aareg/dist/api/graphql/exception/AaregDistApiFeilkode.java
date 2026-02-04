package no.nav.aareg.dist.api.graphql.exception;

import static no.nav.aareg.dist.api.domain.FinnArbeidsforholdVariabler.FIELD_ARBEIDSFORHOLDSTATUS;
import static no.nav.aareg.dist.api.domain.FinnArbeidsforholdVariabler.FIELD_ARBEIDSFORHOLDTYPE;
import static no.nav.aareg.dist.api.domain.FinnArbeidsforholdVariabler.FIELD_ARBEIDSSTED_ID;
import static no.nav.aareg.dist.api.domain.FinnArbeidsforholdVariabler.FIELD_ARBEIDSTAKER_ID;
import static no.nav.aareg.dist.api.domain.FinnArbeidsforholdVariabler.FIELD_OPPLYSNINGSPLIKTIG_ID;
import static no.nav.aareg.dist.api.domain.FinnArbeidsforholdVariabler.FIELD_RAPPORTERINGSORDNING;

public class AaregDistApiFeilkode extends AaregDistFeilkode {

    /**
     * Feilkoder for aareg-dist-api er intervallet [AA-200 - AA-299]
     */

    public static final AaregDistApiFeilkode MANGLENDE_IDENTIFIKATOR = new AaregDistApiFeilkode(
            "AA-200",
            "Spørring må inneholde minst 1 av variablene",
            FIELD_OPPLYSNINGSPLIKTIG_ID + ", " + FIELD_ARBEIDSSTED_ID + ", " + FIELD_ARBEIDSTAKER_ID
    );

    public static final AaregDistApiFeilkode MANGLENDE_VARIABEL_ARBEIDSFORHOLDTYPE = new AaregDistApiFeilkode(
            "AA-201",
            "Spørring må inneholde minst 1 verdi for variabel",
            FIELD_ARBEIDSFORHOLDTYPE
    );

    public static final AaregDistApiFeilkode MANGLENDE_VARIABEL_RAPPORTERINGSORDNING = new AaregDistApiFeilkode(
            "AA-202",
            "Spørring må inneholde minst 1 verdi for variabel",
            FIELD_RAPPORTERINGSORDNING
    );

    public static final AaregDistApiFeilkode MANGLENDE_VARIABEL_ARBEIDSFORHOLDSTATUS = new AaregDistApiFeilkode(
            "AA-203",
            "Spørring må inneholde minst 1 verdi for variabel",
            FIELD_ARBEIDSFORHOLDSTATUS
    );

    public static final AaregDistApiFeilkode UGYLDIG_VARIABEL_OPPLSYNINGSPLIKTIG_ID = new AaregDistApiFeilkode(
            "AA-204",
            "Validering av variabel feilet",
            FIELD_OPPLYSNINGSPLIKTIG_ID
    );

    public static final AaregDistApiFeilkode UGYLDIG_VARIABEL_ARBEIDSSTED_ID = new AaregDistApiFeilkode(
            "AA-205",
            "Validering av variabel feilet",
            FIELD_ARBEIDSSTED_ID
    );

    public static final AaregDistApiFeilkode UGYLDIG_VARIABEL_ARBEIDSTAKER_ID = new AaregDistApiFeilkode(
            "AA-206",
            "Validering av variabel feilet",
            FIELD_ARBEIDSTAKER_ID
    );

    public static final AaregDistApiFeilkode UGYLDIG_VARIABEL_VERDI = new AaregDistApiFeilkode(
            "AA-207",
            "Validering av variabel feilet",
            null
    );

    public AaregDistApiFeilkode(String kode, String beskrivelse, String detaljer) {
        super(kode, beskrivelse, detaljer);
    }
}