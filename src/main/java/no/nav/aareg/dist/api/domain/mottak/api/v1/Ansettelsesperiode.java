package no.nav.aareg.dist.api.domain.mottak.api.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "startdato",
        "sluttdato",
        "sluttaarsak",
        "varsling",
        "gjeldende",
        "sporingsinformasjon"
})
public class Ansettelsesperiode extends Periode {

    private Kodeverksentitet sluttaarsak;

    private Kodeverksentitet varsling;

    private Boolean gjeldende;

    private Sporingsinformasjon sporingsinformasjon;
}
