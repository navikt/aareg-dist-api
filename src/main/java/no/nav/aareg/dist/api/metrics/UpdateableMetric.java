package no.nav.aareg.dist.api.metrics;

import io.micrometer.core.instrument.MeterRegistry;

public interface UpdateableMetric {

    void update(MeterRegistry meterRegistry);
}
