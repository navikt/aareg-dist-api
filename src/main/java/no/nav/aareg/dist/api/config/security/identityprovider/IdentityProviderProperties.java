package no.nav.aareg.dist.api.config.security.identityprovider;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

@Data
@Validated
@ConfigurationProperties("app.security.oidc")
public class IdentityProviderProperties {

    private final Map<String, IdentityProvider> idp = new HashMap<>();
}

