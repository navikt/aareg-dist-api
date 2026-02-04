package no.nav.aareg.dist.api.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@ConditionalOnBean(UpdateableMetric.class)
public class MetricUpdatingScheduler {

    private MeterRegistry meterRegistry;
    private List<UpdateableMetric> metrics;

    @Scheduled(fixedDelay = 1000)
    public void update() {
        metrics.forEach(e -> e.update(meterRegistry));
    }
}
