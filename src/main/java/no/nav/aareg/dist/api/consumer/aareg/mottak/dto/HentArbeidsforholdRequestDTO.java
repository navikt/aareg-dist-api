package no.nav.aareg.dist.api.consumer.aareg.mottak.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HentArbeidsforholdRequestDTO {

    @Getter private String arbeidsforholdId;
    @Getter private String opplysningspliktigId;
    @Getter private String arbeidsstedId;
    @Getter private String arbeidstakerId;
    @Builder.Default private Set<Arbeidsforholdtype> arbeidsforholdtyper = Collections.emptySet();
    @Builder.Default private Set<Rapporteringsordning> rapporteringsordninger = Collections.emptySet();
    @Builder.Default private Set<Arbeidsforholdstatus> arbeidsforholdstatuser = Collections.emptySet();
    @Getter private Boolean historikk;

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

    public Set<Arbeidsforholdtype> getArbeidsforholdtyper() {
        return this.arbeidsforholdtyper != null ? this.arbeidsforholdtyper : new HashSet<>();
    }

    public Set<Rapporteringsordning> getRapporteringsordninger() {
        return this.rapporteringsordninger != null ? this.rapporteringsordninger : new HashSet<>();
    }

    public Set<Arbeidsforholdstatus> getArbeidsforholdstatuser() {
        return this.arbeidsforholdstatuser != null ? this.arbeidsforholdstatuser : new HashSet<>();
    }
}
