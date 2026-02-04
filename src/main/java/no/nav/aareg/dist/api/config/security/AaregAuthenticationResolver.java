package no.nav.aareg.dist.api.config.security;

import com.nimbusds.jwt.JWTParser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.aareg.dist.api.config.security.identityprovider.IdentityProvider;
import no.nav.aareg.dist.api.config.security.identityprovider.IdentityProviderProperties;
import no.nav.aareg.dist.api.config.security.identityprovider.IdentityProviderRegistry;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;
import static org.springframework.security.oauth2.jwt.NimbusJwtDecoder.withJwkSetUri;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(IdentityProviderProperties.class)
public class AaregAuthenticationResolver implements AuthenticationManagerResolver<HttpServletRequest> {

    private static final String SCOPE_DIST_API = "nav:aareg/v1/arbeidsforhold";
    private static final String SCOPE_DIST_API_DATABEHANDLER = "nav:aareg/v1/arbeidsforhold/databehandler";

    private static final List<String> acceptedScopes = List.of(SCOPE_DIST_API, SCOPE_DIST_API_DATABEHANDLER);

    private final BearerTokenResolver resolver = new DefaultBearerTokenResolver();
    private final JwtClaimIssuerConverter issuerConverter = new JwtClaimIssuerConverter();

    private final IdentityProviderRegistry identityProviderRegistry;

    private final ConcurrentHashMap<IdentityProvider, AuthenticationManager> authenticationManagers = new ConcurrentHashMap<>();

    @Override
    public AuthenticationManager resolve(HttpServletRequest context) {
        var issuerName = issuerConverter.convert(context);
        var identityProvider = identityProviderRegistry.getIdp(issuerName);

        if (identityProvider != null) {
            return authenticationManagers.computeIfAbsent(identityProvider, idp -> {
                log.info("Creating AuthenticationManager for unregistered idp, {}", idp);
                return authenticationProvider(idp)::authenticate;
            });
        } else {
            throw new InvalidBearerTokenException(format("Untrusted issuer %s", issuerName));
        }
    }

    private JwtAuthenticationProvider authenticationProvider(IdentityProvider identityProvider) {
        var jwtDecoder = withJwkSetUri(identityProvider.getJwkSetUri()).jwsAlgorithm(SignatureAlgorithm.RS256).build();
        var maskinportenIssuer = "maskinporten";

        if (identityProviderRegistry.getIdpByName(maskinportenIssuer).equals(identityProvider)) {
            jwtDecoder.setJwtValidator(oAuth2TokenValidator(identityProvider.getIssuerUrl()));
        } else {
            jwtDecoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(identityProvider.getIssuerUrl()));
        }

        return new JwtAuthenticationProvider(jwtDecoder);
    }

    private OAuth2TokenValidator<Jwt> oAuth2TokenValidator(String issuer) {
        var issuerValidator = JwtValidators.createDefaultWithIssuer(issuer);

        var scopeValidator = (OAuth2TokenValidator<Jwt>) token ->
                Arrays.stream(token.getClaimAsString("scope").split(" ")).anyMatch(acceptedScopes::contains)
                        ? OAuth2TokenValidatorResult.success()
                        : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", format("None of required scope values '%s' found in token", acceptedScopes), null));

        return new DelegatingOAuth2TokenValidator<>(issuerValidator, scopeValidator);
    }

    private class JwtClaimIssuerConverter implements Converter<HttpServletRequest, String> {

        @Override
        public String convert(@NonNull HttpServletRequest request) {
            try {
                return Optional.ofNullable(
                                JWTParser.parse(resolver.resolve(request))
                                        .getJWTClaimsSet()
                                        .getIssuer())
                        .orElseThrow(() -> new InvalidBearerTokenException("Missing issuer"));
            } catch (Exception ex) {
                throw new InvalidBearerTokenException(ex.getMessage(), ex);
            }
        }
    }
}
