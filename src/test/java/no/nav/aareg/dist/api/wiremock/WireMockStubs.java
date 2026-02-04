package no.nav.aareg.dist.api.wiremock;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(WireMockJUnitExtension.class)
@Inherited
public @interface WireMockStubs {

    Class<? extends WireMockStub>[] value();
}