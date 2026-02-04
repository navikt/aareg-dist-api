package no.nav.aareg.dist.api.domain.mottak.api.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

import static java.util.Comparator.naturalOrder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "kode",
        "beskrivelse"
})
public class Kodeverksentitet implements Comparable<Kodeverksentitet> {

    private String kode;

    private String beskrivelse;

    @Override
    public int compareTo(Kodeverksentitet o) {
        return Objects.compare(this.getKode(), o.getKode(), naturalOrder());
    }
}
