package no.nav.aareg.dist.api.domain.mottak.api.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import static no.nav.aareg.dist.api.domain.mottak.api.v1.Identtype.FOLKEREGISTERIDENT;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Person extends Identer implements Persontype, Opplysningspliktig, Arbeidssted {

    @Override
    @JsonIgnore
    public String getType() {
        return this.getClass().getSimpleName();
    }

    @JsonIgnore
    public String getIdent() {
        return getIdent(FOLKEREGISTERIDENT);
    }
}
