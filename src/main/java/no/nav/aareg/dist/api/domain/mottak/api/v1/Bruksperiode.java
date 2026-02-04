package no.nav.aareg.dist.api.domain.mottak.api.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import no.nav.aareg.dist.api.domain.mottak.api.v1.util.JavaTimeUtil;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@JsonPropertyOrder({
        "fom",
        "tom"
})
public class Bruksperiode {

    private LocalDateTime fom;

    private LocalDateTime tom;

    @JsonIgnore
    public LocalDateTime getFom() {
        return fom;
    }

    @JsonProperty("fom")
    public String getFomAsString() {
        return JavaTimeUtil.toString(fom);
    }

    @JsonProperty("fom")
    public void setFomAsString(String fom) {
        this.fom = JavaTimeUtil.toLocalDateTime(fom);
    }

    @JsonIgnore
    public LocalDateTime getTom() {
        return tom;
    }

    @JsonProperty("tom")
    public String getTomAsString() {
        return JavaTimeUtil.toString(tom);
    }

    @JsonProperty("tom")
    public void setTomAsString(String tom) {
        this.tom = JavaTimeUtil.toLocalDateTime(tom);
    }
}
