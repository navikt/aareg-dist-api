package no.nav.aareg.dist.api.domain.mottak.api.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "id",
        "type",
        "startdato",
        "sluttdato",
        "prosent",
        "varsling",
        "idHistorikk",
        "sporingsinformasjon"
})
public abstract class PermisjonPermittering extends Periode {

    private String id;

    private Kodeverksentitet type;

    private Double prosent;

    private Kodeverksentitet varsling;

    private List<IdHistorikk> idHistorikk;

    private Sporingsinformasjon sporingsinformasjon;
}
