package no.nav.aareg.dist.api.domain.mottak.api.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import no.nav.aareg.dist.api.domain.mottak.api.v1.util.JavaTimeUtil;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode
@ToString
@JsonPropertyOrder({
        "startdato",
        "sluttdato"
})
public class Periode {

    private LocalDate startdato;

    private LocalDate sluttdato;

    @JsonIgnore
    public LocalDate getStartdato() {
        return startdato;
    }

    @JsonProperty("startdato")
    public String getStartdatoAsString() {
        return JavaTimeUtil.toString(startdato);
    }

    @JsonProperty("startdato")
    public void setStartdatoAsString(String startdato) {
        this.startdato = JavaTimeUtil.toLocalDate(startdato);
    }

    @JsonIgnore
    public LocalDate getSluttdato() {
        return sluttdato;
    }

    @JsonProperty("sluttdato")
    public String getSluttdatoAsString() {
        return JavaTimeUtil.toString(sluttdato);
    }

    @JsonProperty("sluttdato")
    public void setSluttdatoAsString(String sluttdato) {
        this.sluttdato = JavaTimeUtil.toLocalDate(sluttdato);
    }
}
