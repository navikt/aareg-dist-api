package no.nav.aareg.dist.api.domain.mottak.api.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import no.nav.aareg.dist.api.domain.mottak.api.v1.util.JavaTimeUtil;

import java.time.LocalDate;
import java.time.YearMonth;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = OrdinaerAnsettelsesdetaljer.class, name = OrdinaerAnsettelsesdetaljer.TYPE),
        @JsonSubTypes.Type(value = MaritimAnsettelsesdetaljer.class, name = MaritimAnsettelsesdetaljer.TYPE),
        @JsonSubTypes.Type(value = ForenkletAnsettelsesdetaljer.class, name = ForenkletAnsettelsesdetaljer.TYPE),
        @JsonSubTypes.Type(value = FrilanserAnsettelsesdetaljer.class, name = FrilanserAnsettelsesdetaljer.TYPE)
})
public abstract class Ansettelsesdetaljer implements Ansettelsesdetaljertype {

    private Kodeverksentitet arbeidstidsordning;

    private Kodeverksentitet ansettelsesform;

    private Kodeverksentitet yrke;

    private Double antallTimerPrUke;

    private Double avtaltStillingsprosent;

    private LocalDate sisteStillingsprosentendring;

    private LocalDate sisteLoennsendring;

    private Rapporteringsmaaneder rapporteringsmaaneder;

    private Sporingsinformasjon sporingsinformasjon;

    @JsonIgnore
    public LocalDate getSisteStillingsprosentendring() {
        return sisteStillingsprosentendring;
    }

    @JsonProperty("sisteStillingsprosentendring")
    public String getSisteStillingsprosentendringAsString() {
        return JavaTimeUtil.toString(sisteStillingsprosentendring);
    }

    @JsonProperty("sisteStillingsprosentendring")
    public void setSisteStillingsprosentendringAsString(String sisteStillingsprosentendring) {
        this.sisteStillingsprosentendring = JavaTimeUtil.toLocalDate(sisteStillingsprosentendring);
    }

    @JsonIgnore
    public LocalDate getSisteLoennsendring() {
        return sisteLoennsendring;
    }

    @JsonProperty("sisteLoennsendring")
    public String getSisteLoennsendringAsString() {
        return JavaTimeUtil.toString(sisteLoennsendring);
    }

    @JsonProperty("sisteLoennsendring")
    public void setSisteLoennsendringAsString(String sisteLoennsendring) {
        this.sisteLoennsendring = JavaTimeUtil.toLocalDate(sisteLoennsendring);
    }

    @JsonIgnore
    public YearMonth getRapporteringsmaanedFra() {
        return rapporteringsmaaneder != null ? rapporteringsmaaneder.getFra() : null;
    }

    @JsonIgnore
    public YearMonth getRapporteringsmaanedTil() {
        return rapporteringsmaaneder != null ? rapporteringsmaaneder.getTil() : null;
    }
}