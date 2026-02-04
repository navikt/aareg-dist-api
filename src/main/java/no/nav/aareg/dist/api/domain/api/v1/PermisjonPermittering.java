package no.nav.aareg.dist.api.domain.api.v1;

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
public abstract class PermisjonPermittering extends Periode {

    private String id;

    private Kodeverksentitet type;

    private Double prosent;

    private Kodeverksentitet varsling;

    private List<IdHistorikk> idHistorikk;
}
