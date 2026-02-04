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
        "land",
        "startdato",
        "sluttdato",
        "rapporteringsmaaned",
        "sporingsinformasjon"
})
public class Utenlandsopphold extends Periode {

    private Kodeverksentitet land;

    private PersistentYearMonth rapporteringsmaaned;

    private Sporingsinformasjon sporingsinformasjon;
}
