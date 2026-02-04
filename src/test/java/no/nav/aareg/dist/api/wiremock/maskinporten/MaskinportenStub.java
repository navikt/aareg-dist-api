package no.nav.aareg.dist.api.wiremock.maskinporten;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.extension.Extension;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import net.minidev.json.JSONObject;
import no.nav.aareg.dist.api.wiremock.WireMockStub;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.nimbusds.jose.JWSAlgorithm.RS256;
import static java.time.Instant.now;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class MaskinportenStub implements WireMockStub {

    public static final String MASKINPORTEN_SKATTEETATEN_TOKEN_VALUE = "Bearer token";

    private static final String MASKINPORTEN_CONSUMER = "815493000";
    private static final String MASKINPORTEN_SUPPLIER = "815493001";

    private final MaskinportenResponseTransformer responseTransformer;

    private final RSAKey key;
    private final RSASSASigner signer;
    private final JWSHeader jwsHeader;

    public MaskinportenStub() {
        responseTransformer = new MaskinportenResponseTransformer(this);

        try {
            key = new RSAKeyGenerator(2048).keyID(UUID.randomUUID().toString()).generate();
            signer = new RSASSASigner(key);
            jwsHeader = new JWSHeader.Builder(RS256).keyID(key.getKeyID()).build();
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to create maskinporten-stub", exception);
        }
    }

    @Override
    public Extension getWireMockExtension() {
        return responseTransformer;
    }

    @Override
    public void registerResponseMappingBeforeAll() {
        WireMock.stubFor(get(urlPathMatching("/maskinporten/(.*)"))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withTransformers(responseTransformer.getName())));
        WireMock.stubFor(post(urlPathMatching("/maskinporten/(.*)"))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withTransformers(responseTransformer.getName())));
    }

    @Override
    public void registerResponseMappingBeforeEach() {
        registerResponseMappingBeforeAll();
    }

    public String getWellKnownOpenidConfiguration() {
        return "{" +
                "  \"issuer\": \"http://localhost:" + getPort() + "/maskinporten\"," +
                "  \"jwks_uri\": \"http://localhost:" + getPort() + "/maskinporten/jwks\"" +
                "}";
    }

    public String getWellKnownOauthConfiguration() {
        return "{" +
                "  \"issuer\": \"http://localhost:" + getPort() + "/maskinporten\"," +
                "  \"jwks_uri\": \"http://localhost:" + getPort() + "/maskinporten/jwks\"," +
                "  \"token_endpoint\": \"http://localhost:" + getPort() + "/maskinporten/token\"" +
                "}";
    }

    public String getJwks() {
        return "{" +
                "  \"keys\": [" + key.toPublicJWK().toJSONString() + "]" +
                "}";
    }

    public String getAccesstoken() {
        var localDateNow = LocalDate.now();
        return "{" +
                "  \"access_token\": \"token\"," +
                "  \"expires_in\": \"" + LocalTime.now().plusMinutes(2).toEpochSecond(localDateNow, ZoneOffset.MIN) + "\"" +
                "}";
    }

    public String generateToken(String scope) {
        return generateToken(createDefaultClaims(scope));
    }

    public String generateTokenWithout(String scope, String... claimsToExclude) {
        Map<String, Object> claims = createDefaultClaims(scope);
        Arrays.asList(claimsToExclude).forEach(claims::remove);
        return generateToken(claims);
    }

    public String generateTokenWithOverride(String scope, String claim, Object overrideValue) {
        Map<String, Object> claims = createDefaultClaims(scope);
        claims.put(claim, overrideValue);
        return generateToken(claims);
    }

    private String generateToken(Map<String, Object> claims) {
        try {
            JWTClaimsSet.Builder claimsSetBuilder = new JWTClaimsSet.Builder();
            claims.forEach(claimsSetBuilder::claim);

            SignedJWT signedToken = new SignedJWT(jwsHeader, claimsSetBuilder.build());
            signedToken.sign(signer);
            return signedToken.serialize();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to generate JWT token", exception);
        }
    }

    private Map<String, Object> createDefaultClaims(String scope) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("client_id", "test_client");
        claims.put("aud", "unspecified");
        claims.put("iss", "http://localhost:" + getPort() + "/maskinporten");
        claims.put("jti", "1337");
        claims.put("exp", now().plusSeconds(60).getEpochSecond());
        claims.put("client_orgno", "123456789");
        claims.put("consumer", new JSONObject(Map.of("authority", "iso", "ID", "0192:" + MASKINPORTEN_CONSUMER)));
        claims.put("supplier", new JSONObject(Map.of("authority", "iso", "ID", "0192:" + MASKINPORTEN_SUPPLIER)));
        claims.put("scope", scope);
        return claims;
    }

    private String getPort() {
        return System.getProperty("wiremock.server.port");
    }
}
