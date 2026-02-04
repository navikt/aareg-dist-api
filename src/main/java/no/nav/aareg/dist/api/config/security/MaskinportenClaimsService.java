package no.nav.aareg.dist.api.config.security;

import no.nav.aareg.dist.api.config.security.exception.MaskinportenTokenException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import static com.nimbusds.jose.util.JSONObjectUtils.getJSONObject;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Component
public class MaskinportenClaimsService {

    public static final String CONSUMER = "consumer";
    public static final String SUPPLIER = "supplier";

    public Map<String, String> hentOrgnrFraToken() {
        var orgNrSett = new HashMap<String, String>();
        var principal = getContext().getAuthentication().getPrincipal();
        var claims = principal instanceof Jwt jwt ? jwt.getClaims() : new HashMap<String, Object>();

        if (claims.containsKey(CONSUMER)) {
            orgNrSett.put(CONSUMER, hentOrgnrFraClaim(claims, CONSUMER));
        } else {
            throw new MaskinportenTokenException("Token mangler organisasjonsnummer for konsument");
        }

        if (claims.containsKey(SUPPLIER)) {
            orgNrSett.put(SUPPLIER, hentOrgnrFraClaim(claims, SUPPLIER));
        }

        return orgNrSett;
    }

    private String hentOrgnrFraClaim(Map<String, Object> claims, String key) {
        try {
            return getJSONObject(claims, key).get("ID").toString().split(":")[1];
        } catch (ParseException e) {
            throw new MaskinportenTokenException("Uventet feil ved parsing av claims");
        }
    }
}
