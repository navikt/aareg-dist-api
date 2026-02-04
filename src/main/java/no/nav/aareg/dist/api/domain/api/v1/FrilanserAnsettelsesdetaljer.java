package no.nav.aareg.dist.api.domain.api.v1;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FrilanserAnsettelsesdetaljer extends Ansettelsesdetaljer {

    public static final String TYPE = "Frilanser";

    @Override
    public String getType() {
        return TYPE;
    }
}
