package no.nav.aareg.dist.api.domain.api.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class Person implements Persontype, Opplysningspliktig, Arbeidssted {

    public static final String TYPE = "Person";

    private String ident;

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
