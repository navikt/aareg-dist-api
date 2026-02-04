package no.nav.aareg.dist.api.domain.dto;

import java.util.Set;

public class Arbeidsforholdtype {

    public static final String ORDINAERT_ARBEIDSFORHOLD = "ordinaertArbeidsforhold";
    public static final String MARITIMT_ARBEIDSFORHOLD = "maritimtArbeidsforhold";
    public static final String FORENKLET_OPPGJOERSORDNING = "forenkletOppgjoersordning";
    public static final String FRILANSER_ARBEIDSFORHOLD = "frilanserOppdragstakerHonorarPersonerMm";

    public static final Set<String> ARBEIDSFORHOLDTYPER_ALLE = Set.of(
            ORDINAERT_ARBEIDSFORHOLD,
            MARITIMT_ARBEIDSFORHOLD,
            FORENKLET_OPPGJOERSORDNING,
            FRILANSER_ARBEIDSFORHOLD
    );

    private Arbeidsforholdtype() {
    }
}
