package no.nav.aareg.dist.api.domain.mottak.api.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.StringUtils;

import java.time.YearMonth;
import java.util.Comparator;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@JsonPropertyOrder({
        "fra",
        "til"
})
public class Rapporteringsmaaneder {

    public static final Comparator<Rapporteringsmaaneder> RAPPORTERINGSMAANEDER_COMPARATOR = comparing(Rapporteringsmaaneder::getFra, nullsLast(naturalOrder()))
            .thenComparing(Rapporteringsmaaneder::getTil, nullsLast(naturalOrder()));

    private PersistentYearMonth fra;

    private PersistentYearMonth til;

    @JsonProperty("fra")
    public String getFraAsString() {
        return fra != null ? fra.toString() : null;
    }

    @JsonProperty("fra")
    public void setFraAsString(String fra) {
        this.fra = StringUtils.hasText(fra) ? new PersistentYearMonth(fra) : null;
    }

    @JsonProperty("til")
    public String getTilAsString() {
        return til != null ? til.toString() : null;
    }

    @JsonProperty("til")
    public void setTilAsString(String til) {
        this.til = StringUtils.hasText(til) ? new PersistentYearMonth(til) : null;
    }

    @JsonIgnore
    public YearMonth getFra() {
        if (fra == null || fra.getYear() == null) {
            return null;
        }
        return YearMonth.of(fra.getYear(), fra.getMonth());
    }

    @JsonIgnore
    public YearMonth getTil() {
        if (til == null || til.getYear() == null) {
            return null;
        }
        return YearMonth.of(til.getYear(), til.getMonth());
    }
}