package no.nav.aareg.dist.api.domain.api.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

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
}
