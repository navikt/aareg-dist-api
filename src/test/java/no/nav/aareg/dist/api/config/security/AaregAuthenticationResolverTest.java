package no.nav.aareg.dist.api.config.security;

import no.nav.aareg.dist.api.config.security.identityprovider.IdentityProvider;
import no.nav.aareg.dist.api.config.security.identityprovider.IdentityProviderRegistry;
import no.nav.aareg.dist.api.wiremock.WireMockStubs;
import no.nav.aareg.dist.api.wiremock.maskinporten.MaskinportenStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.Instant.now;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@WireMockStubs(MaskinportenStub.class)
class AaregAuthenticationResolverTest {

    private static final String CLIENT_ID = "expected-client-id";
    private static final String SCOPE = "nav:aareg/v1/arbeidsforhold";

    private MaskinportenStub maskinportenStub;
    private AaregAuthenticationResolver resolver;
    private IdentityProviderRegistry registry;
    private String azureIssuer;
    private String maskinportenIssuer;

    @BeforeEach
    void setUp() {
        var baseUrl = "http://localhost:" + System.getProperty("wiremock.server.port");
        azureIssuer = baseUrl + "/azuread";
        maskinportenIssuer = baseUrl + "/maskinporten";
        var jwksUri = baseUrl + "/maskinporten/jwks";
        registry = new IdentityProviderRegistry(Map.of(
                "maskinporten", identityProvider(maskinportenIssuer, jwksUri),
                "azuread", identityProvider(azureIssuer, jwksUri)
        ));
        resolver = new AaregAuthenticationResolver(registry, CLIENT_ID);
    }

    @Test
    void godtarEntraTokenMedRiktigMottaker() {
        assertTrue(authenticate(entraClaims()).isAuthenticated());
    }

    @Test
    void godtarEntraTokenMedRiktigMottakerIListe() {
        var claims = entraClaims();
        claims.put("aud", List.of("another-client", CLIENT_ID));

        assertTrue(authenticate(claims).isAuthenticated());
    }

    @Test
    void avviserEntraTokenUtenMottaker() {
        var claims = entraClaims();
        claims.remove("aud");

        assertThrows(InvalidBearerTokenException.class, () -> authenticate(claims));
    }

    @Test
    void avviserEntraTokenMedTomMottaker() {
        var claims = entraClaims();
        claims.put("aud", "");

        assertThrows(InvalidBearerTokenException.class, () -> authenticate(claims));
    }

    @Test
    void avviserEntraTokenMedFeilMottaker() {
        var claims = entraClaims();
        claims.put("aud", "another-client");

        assertThrows(InvalidBearerTokenException.class, () -> authenticate(claims));
    }

    @Test
    void avviserEntraTokenMedTomMottakerliste() {
        var claims = entraClaims();
        claims.put("aud", List.of());

        assertThrows(InvalidBearerTokenException.class, () -> authenticate(claims));
    }

    @Test
    void avviserUtloeptEntraToken() {
        var claims = entraClaims();
        claims.put("exp", now().minusSeconds(120).getEpochSecond());

        assertThrows(InvalidBearerTokenException.class, () -> authenticate(claims));
    }

    @Test
    void avviserEntraTokenFoerGyldighetsperioden() {
        var claims = entraClaims();
        claims.put("nbf", now().plusSeconds(180).getEpochSecond());

        assertThrows(InvalidBearerTokenException.class, () -> authenticate(claims));
    }

    @Test
    void avviserEntraTokenMedUkjentSigneringsnoekkel() {
        var token = new MaskinportenStub().generateToken(entraClaims());

        assertThrows(InvalidBearerTokenException.class, () -> authenticate(token));
    }

    @Test
    void avviserTokenFraUkjentUtsteder() {
        var claims = entraClaims();
        claims.put("iss", "https://untrusted.invalid");

        assertThrows(InvalidBearerTokenException.class, () -> authenticate(claims));
    }

    @Test
    void godtarMaskinportenTokenMedArbeidsforholdScope() {
        var claims = claims(maskinportenIssuer);
        claims.put("scope", SCOPE);

        assertTrue(authenticate(claims).isAuthenticated());
    }

    @Test
    void godtarMaskinportenTokenMedDatabehandlerScope() {
        var claims = claims(maskinportenIssuer);
        claims.put("scope", "nav:aareg/v1/arbeidsforhold/databehandler");

        assertTrue(authenticate(claims).isAuthenticated());
    }

    @Test
    void avviserMaskinportenTokenMedFeilScope() {
        var claims = claims(maskinportenIssuer);
        claims.put("scope", "another-scope");
        claims.put("aud", CLIENT_ID);

        assertThrows(InvalidBearerTokenException.class, () -> authenticate(claims));
    }

    @Test
    void avviserManglendeKlientId() {
        assertThrows(IllegalArgumentException.class, () -> new AaregAuthenticationResolver(registry, null));
    }

    @Test
    void avviserTomKlientId() {
        assertThrows(IllegalArgumentException.class, () -> new AaregAuthenticationResolver(registry, ""));
    }

    @Test
    void avviserKlientIdMedBareMellomrom() {
        assertThrows(IllegalArgumentException.class, () -> new AaregAuthenticationResolver(registry, " "));
    }

    private Authentication authenticate(Map<String, Object> claims) {
        return authenticate(maskinportenStub.generateToken(claims));
    }

    private Authentication authenticate(String token) {
        var request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION, "Bearer " + token);
        return resolver.resolve(request).authenticate(new BearerTokenAuthenticationToken(token));
    }

    private Map<String, Object> entraClaims() {
        var claims = claims(azureIssuer);
        claims.put("aud", CLIENT_ID);
        return claims;
    }

    private static Map<String, Object> claims(String issuer) {
        var claims = new HashMap<String, Object>();
        claims.put("iss", issuer);
        claims.put("exp", now().plusSeconds(600).getEpochSecond());
        return claims;
    }

    private static IdentityProvider identityProvider(String issuer, String jwksUri) {
        var identityProvider = new IdentityProvider();
        identityProvider.setIssuerUrl(issuer);
        identityProvider.setJwkSetUri(jwksUri);
        return identityProvider;
    }
}
