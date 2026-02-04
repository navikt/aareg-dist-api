package no.nav.aareg.dist.api.domain.mottak.api.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonPropertyOrder({
        "arbeidstidsordning",
        "ansettelsesform",
        "yrke",
        "antallTimerPrUke",
        "avtaltStillingsprosent",
        "sisteStillingsprosentendring",
        "sisteLoennsendring",
        "rapporteringsmaaneder",
        "sporingsinformasjon"
})
public class OrdinaerAnsettelsesdetaljer extends Ansettelsesdetaljer {

    public static final String TYPE = "Ordinaer";

    @Override
    @JsonIgnore
    public String getType() {
        return TYPE;
    }
}
