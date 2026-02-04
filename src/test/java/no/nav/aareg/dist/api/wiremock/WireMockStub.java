package no.nav.aareg.dist.api.wiremock;

import com.github.tomakehurst.wiremock.extension.Extension;

public interface WireMockStub {

    default Extension getWireMockExtension() {
        return null;
    }

    default void registerResponseMappingBeforeAll() {
    }

    default void registerResponseMappingBeforeEach() {
    }
}
