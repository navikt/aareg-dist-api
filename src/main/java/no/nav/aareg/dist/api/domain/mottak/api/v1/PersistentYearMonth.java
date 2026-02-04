package no.nav.aareg.dist.api.domain.mottak.api.v1;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.time.YearMonth;

/**
 * MongoDB er ikke så glad i YearMonth pr Des 2023, så laget en custom implementasjon
 */
@Getter
@Setter
@Builder
@EqualsAndHashCode
@JsonPropertyOrder({
        "year",
        "month"
})
public class PersistentYearMonth {

    private Integer year = null;
    private Integer month = null;

    public PersistentYearMonth() {
        var now = YearMonth.now();
        this.year = now.getYear();
        this.month = now.getMonthValue();
    }

    public PersistentYearMonth(YearMonth rapporteringsmaaned) {
        if (rapporteringsmaaned != null) {
            this.year = rapporteringsmaaned.getYear();
            this.month = rapporteringsmaaned.getMonthValue();
        }
    }

    public PersistentYearMonth(int year, int monthValue) {
        this.year = year;
        this.month = monthValue;
    }

    public PersistentYearMonth(String yearMonth) {
        if (StringUtils.hasText(yearMonth) && yearMonth.contains("-")) {
            String[] value = yearMonth.split("-");
            this.year = Integer.valueOf(value[0]);
            this.month = Integer.valueOf(value[1]);
        } else {
            throw new IllegalArgumentException("Ikke gyldig PersistentYearMonth. Benytt følgende format yyyy-mm");
        }
    }

    public YearMonth toYearMonth() {
        return YearMonth.of(year, month);
    }

    public String toString() {
        if (this.getYear() == null || this.getMonth() == null) {
            return null;
        }
        return this.getYear() + "-" + this.getMonth();
    }
}
