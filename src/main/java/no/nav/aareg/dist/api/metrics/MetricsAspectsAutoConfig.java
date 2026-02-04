package no.nav.aareg.dist.api.metrics;

import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.micrometer.metrics.autoconfigure.MetricsAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.Collections.emptyList;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ ProceedingJoinPoint.class, MeterRegistry.class })
@AutoConfigureAfter(MetricsAutoConfiguration.class)
public class MetricsAspectsAutoConfig {

    @Bean
    TimedAspect timedAspect(MeterRegistry meterRegistry) {
        return new TimedAspect(meterRegistry,
                proceedingJoinPoint -> emptyList(),
                proceedingJoinPoint -> false
        );
    }

    @Bean
    CountedAspect countedAspect(MeterRegistry meterRegistry) {
        return new CountedAspect(meterRegistry,
                proceedingJoinPoint -> emptyList(),
                proceedingJoinPoint -> false
        );
    }
}
