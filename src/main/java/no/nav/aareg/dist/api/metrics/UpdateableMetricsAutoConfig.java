package no.nav.aareg.dist.api.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@EnableScheduling
@Configuration(proxyBeanMethods = false)
@ConditionalOnBean(UpdateableMetric.class)
public class UpdateableMetricsAutoConfig {

    @Bean
    MetricUpdatingScheduler metricUpdatingScheduler(MeterRegistry meterRegistry, List<UpdateableMetric> updateableMetrics) {
        return new MetricUpdatingScheduler(meterRegistry, updateableMetrics);
    }
}
