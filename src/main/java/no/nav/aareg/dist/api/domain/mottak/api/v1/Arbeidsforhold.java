package no.nav.aareg.dist.api.domain.mottak.api.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import no.nav.aareg.dist.api.domain.mottak.api.v1.util.JavaTimeUtil;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.reverseOrder;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;
import static java.util.Comparator.nullsLast;
import static java.util.Objects.isNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "id",
        "type",
        "arbeidstaker",
        "arbeidssted",
        "opplysningspliktig",
        "ansettelsesperiode",
        "ansettelsesdetaljer",
        "permisjoner",
        "permitteringer",
        "timerMedTimeloenn",
        "utenlandsopphold",
        "idHistorikk",
        "varsler",
        "rapporteringsordning",
        "navArbeidsforholdId",
        "navVersjon",
        "navUuid",
        "opprettet",
        "sistBekreftet",
        "sistEndret",
        "sistSynkronisert",
        "bruksperiode",
        "sporingsinformasjon"
})
public class Arbeidsforhold {

    public static final String COLLECTION_NAME = "arbeidsforhold";

    public static final String ARBEIDSFORHOLD_UUID_FELT = "navUuid";

    public static final String ARBEIDSTAKER_IDENT_FELT = "arbeidstaker.identer.ident";

    public static final String ANSETTELSESPERIODE_STARTDATO_FELT = "ansettelsesperiode.startdato";
    public static final String ANSETTELSESPERIODE_SLUTTDATO_FELT = "ansettelsesperiode.sluttdato";

    public static final String ARBEIDSFORHOLDTYPE_FELT = "type.kode";
    public static final String ARBEIDSSTED_IDENT_FELT = "arbeidssted.identer.ident";

    public static final String OPPLYSNINGSPLIKTIG_IDENT_FELT = "opplysningspliktig.identer.ident";
    public static final String RAPPORTERINGSORDNING_FELT = "rapporteringsordning.kode";

    public static final Comparator<Arbeidsforhold> OPPRETTET_COMPARATOR = comparing(Arbeidsforhold::getOpprettet, nullsFirst(naturalOrder()));

    private String id;

    private Kodeverksentitet type;

    private Arbeidstaker arbeidstaker;

    private Arbeidssted arbeidssted;

    private Opplysningspliktig opplysningspliktig;

    private Ansettelsesperiode ansettelsesperiode;

    private List<Ansettelsesdetaljer> ansettelsesdetaljer;

    private List<Permisjon> permisjoner;

    private List<Permittering> permitteringer;

    private List<TimerMedTimeloenn> timerMedTimeloenn;

    private List<Utenlandsopphold> utenlandsopphold;

    private List<IdHistorikk> idHistorikk;

    private List<Varsel> varsler;

    private Kodeverksentitet rapporteringsordning;

    private Long navArbeidsforholdId;

    private Integer navVersjon;

    private String navUuid;

    private LocalDateTime opprettet;

    private LocalDateTime sistBekreftet;

    private LocalDateTime sistEndret;

    private LocalDateTime sistSynkronisert;

    private Bruksperiode bruksperiode;

    private Sporingsinformasjon sporingsinformasjon;

    public List<Ansettelsesdetaljer> getAnsettelsesdetaljer() {
        return ansettelsesdetaljer != null ? ansettelsesdetaljer : emptyList();
    }

    @JsonIgnore
    public Ansettelsesdetaljer getGjeldendeAnsettelsesdetaljer() {
        return getAnsettelsesdetaljer().stream()
                .filter(ans -> isNull(ans.getRapporteringsmaanedTil()))
                .findAny()
                .orElse(null);
    }

    @JsonIgnore
    public List<Ansettelsesdetaljer> getAnsettelsesdetaljerhistorikk() {
        return getAnsettelsesdetaljer().stream()
                .filter(ans -> !isNull(ans.getRapporteringsmaanedTil()))
                .sorted(comparing(Ansettelsesdetaljer::getRapporteringsmaanedFra, nullsLast(reverseOrder())))
                .toList();
    }

    @JsonIgnore
    public LocalDateTime getOpprettet() {
        return opprettet;
    }

    @JsonProperty("opprettet")
    public String getOpprettetAsString() {
        return JavaTimeUtil.toString(opprettet);
    }

    @JsonProperty("opprettet")
    public void setOpprettetAsString(String opprettet) {
        this.opprettet = JavaTimeUtil.toLocalDateTime(opprettet);
    }

    @JsonIgnore
    public LocalDateTime getSistBekreftet() {
        return sistBekreftet;
    }

    @JsonProperty("sistBekreftet")
    public String getSistBekreftetAsString() {
        return JavaTimeUtil.toString(sistBekreftet);
    }

    @JsonProperty("sistBekreftet")
    public void setSistBekreftetAsString(String sistBekreftet) {
        this.sistBekreftet = JavaTimeUtil.toLocalDateTime(sistBekreftet);
    }

    @JsonIgnore
    public LocalDateTime getSistEndret() {
        return sistEndret;
    }

    @JsonProperty("sistEndret")
    public String getSistEndretAsString() {
        return JavaTimeUtil.toString(sistEndret);
    }

    @JsonProperty("sistEndret")
    public void setSistEndretAsString(String sistEndret) {
        this.sistEndret = JavaTimeUtil.toLocalDateTime(sistEndret);
    }

    @JsonIgnore
    public LocalDateTime getSistSynkronisert() {
        return sistSynkronisert;
    }

    @JsonProperty("sistSynkronisert")
    public String getSistSynkronisertAsString() {
        return JavaTimeUtil.toString(sistSynkronisert);
    }

    @JsonProperty("sistSynkronisert")
    public void setSistSynkronisertAsString(String sistEndret) {
        this.sistSynkronisert = JavaTimeUtil.toLocalDateTime(sistEndret);
    }

    public String hentGjeldendeOpplysningspliktigId() {
        if (opplysningspliktig instanceof Hovedenhet hovedenhet) {
            return hovedenhet.getIdent();
        } else {
            return ((Person) opplysningspliktig).getGjeldendeIdent(Identtype.FOLKEREGISTERIDENT);
        }
    }

    public String hentGjeldendeArbeidsstedId() {
        if (arbeidssted instanceof Underenhet underenhet) {
            return underenhet.getIdent();
        } else {
            return ((Person) arbeidssted).getGjeldendeIdent(Identtype.FOLKEREGISTERIDENT);
        }
    }

    public String hentGjeldendeArbeidstakerId() {
        return arbeidstaker.getGjeldendeIdent(Identtype.FOLKEREGISTERIDENT);
    }
}