package no.nav.aareg.dist.api.consumer.texas.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TexasResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") Integer expiresIn
) {
}
