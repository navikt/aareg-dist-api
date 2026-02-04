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
import no.nav.aareg.dist.api.domain.mottak.api.v1.util.JavaTimeUtil;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@JsonPropertyOrder({
        "opprettetTidspunkt",
        "opprettetAv",
        "opprettetKilde",
        "opprettetKildereferanse",
        "endretTidspunkt",
        "endretAv",
        "endretKilde",
        "endretKildereferanse"
})
public class Sporingsinformasjon {

    private LocalDateTime opprettetTidspunkt;

    private String opprettetAv;

    private String opprettetKilde;

    private String opprettetKildereferanse;

    private LocalDateTime endretTidspunkt;

    private String endretAv;

    private String endretKilde;

    private String endretKildereferanse;

    @JsonIgnore
    public LocalDateTime getOpprettetTidspunkt() {
        return opprettetTidspunkt;
    }

    @JsonProperty("opprettetTidspunkt")
    public String getOpprettetTidspunktAsString() {
        return JavaTimeUtil.toString(opprettetTidspunkt);
    }

    @JsonProperty("opprettetTidspunkt")
    public void setOpprettetTidspunktAsString(String opprettetTidspunkt) {
        this.opprettetTidspunkt = JavaTimeUtil.toLocalDateTime(opprettetTidspunkt);
    }

    @JsonIgnore
    public LocalDateTime getEndretTidspunkt() {
        return endretTidspunkt;
    }

    @JsonProperty("endretTidspunkt")
    public String getEndretTidspunktAsString() {
        return JavaTimeUtil.toString(endretTidspunkt);
    }

    @JsonProperty("endretTidspunkt")
    public void setEndretTidspunktAsString(String endretTidspunkt) {
        this.endretTidspunkt = JavaTimeUtil.toLocalDateTime(endretTidspunkt);
    }
}
