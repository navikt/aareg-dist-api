package no.nav.aareg.dist.api.config.security.identityprovider;

import lombok.Data;

@Data
public class IdentityProvider {

    private String issuerUrl;
    private String jwkSetUri;
}
