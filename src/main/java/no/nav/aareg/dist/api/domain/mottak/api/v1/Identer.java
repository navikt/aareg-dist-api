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
import java.util.Objects;

import static java.lang.Boolean.TRUE;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "identer"
})
public abstract class Identer {

    private List<Ident> identer;

    public String getIdent(Identtype identtype) {
        return identer.stream()
                .filter(ident -> Objects.equals(identtype, ident.getType()))
                .findFirst()
                .map(Ident::getIdent)
                .orElse(null);
    }

    public String getGjeldendeIdent(Identtype identtype) {
        return identer.stream()
                .filter(ident -> Objects.equals(ident.getGjeldende(), TRUE) && Objects.equals(identtype, ident.getType()))
                .findFirst()
                .map(Ident::getIdent)
                .orElse(null);
    }
}
