package no.nav.aareg.dist.api.consumer.texas.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenRequest(
        @JsonProperty("identity_provider") String identityProvider,
        String target
) {
}
