package no.nav.aareg.dist.api.config.security.identityprovider;

import no.nav.aareg.dist.api.config.security.exception.OidcException;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static org.springframework.util.StringUtils.hasText;

public class IdentityProviderRegistry {

    private final Map<String, IdentityProvider> idpByIssuerMap = new HashMap<>();
    private final Map<String, IdentityProvider> idpByNameMap = new HashMap<>();

    public IdentityProviderRegistry(Map<String, IdentityProvider> identityProviders) {
        identityProviders.forEach(this::add);
        idpByNameMap.putAll(identityProviders);
    }

    private void add(String name, IdentityProvider identityProvider) {
        if (!hasText(identityProvider.getIssuerUrl())) {
            throw new OidcException(format("IdentityProvider with name: %s does not have issuer url", name));
        }
        if (!hasText(identityProvider.getJwkSetUri())) {
            throw new OidcException(format("IdentityProvider with name: %s does not have jwkSet uri", name));
        }
        idpByIssuerMap.put(identityProvider.getIssuerUrl(), identityProvider);
    }

    public IdentityProvider getIdp(String idpName) {
        return idpByIssuerMap.get(idpName);
    }

    public IdentityProvider getIdpByName(String idpName) {
        return idpByNameMap.get(idpName);
    }
}
