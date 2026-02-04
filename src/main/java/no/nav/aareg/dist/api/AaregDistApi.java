package no.nav.aareg.dist.api;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import static org.springframework.boot.Banner.Mode.OFF;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AaregDistApi {

    static void main(String[] args) {
        new SpringApplicationBuilder()
                .bannerMode(OFF)
                .sources(AaregDistApi.class)
                .registerShutdownHook(true)
                .run(args);
    }
}
