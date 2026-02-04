package no.nav.aareg.dist.api.consumer.texas.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ExchangeRequest(
        @JsonProperty("identity_provider") String identityProvider,
        String target,
        @JsonProperty("user_token") String userToken
) {
}
