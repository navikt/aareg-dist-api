package no.nav.aareg.dist.api.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinnArbeidsforholdVariabler {

    public static final String FIELD_OPPLYSNINGSPLIKTIG_ID = "opplysningspliktigId";
    public static final String FIELD_ARBEIDSSTED_ID = "arbeidsstedId";
    public static final String FIELD_ARBEIDSTAKER_ID = "arbeidstakerId";
    public static final String FIELD_ARBEIDSFORHOLDTYPE = "arbeidsforholdtype";
    public static final String FIELD_RAPPORTERINGSORDNING = "rapporteringsordning";
    public static final String FIELD_ARBEIDSFORHOLDSTATUS = "arbeidsforholdstatus";
    public static final String FIELD_ANSETTELSESDETALJERHISTORIKK = "ansettelsesdetaljerhistorikk";

    private String opplysningspliktigId;
    private String arbeidsstedId;
    private String arbeidstakerId;

    private Set<Arbeidsforholdtype> arbeidsforholdtype;
    private Set<Rapporteringsordning> rapporteringsordning;
    private Set<Arbeidsforholdstatus> arbeidsforholdstatus;

    private Boolean ansettelsesdetaljerhistorikk;

    public enum Arbeidsforholdstatus {
        AKTIV,
        AVSLUTTET,
        FREMTIDIG
    }

    public enum Arbeidsforholdtype {
        ordinaertArbeidsforhold,
        maritimtArbeidsforhold,
        frilanserOppdragstakerHonorarPersonerMm,
        forenkletOppgjoersordning
    }

    public enum Rapporteringsordning {
        A_ORDNINGEN,
        FOER_A_ORDNINGEN
    }
}
