package no.nav.aareg.dist.api.domain.api.v1;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MaritimAnsettelsesdetaljer extends Ansettelsesdetaljer {

    public static final String TYPE = "Maritim";

    private Kodeverksentitet fartsomraade;

    private Kodeverksentitet skipsregister;

    private Kodeverksentitet fartoeystype;

    @Override
    public String getType() {
        return TYPE;
    }
}
