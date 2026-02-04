package no.nav.aareg.dist.api.config.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class OidcException extends RuntimeException {

    public OidcException(String message) {
        super(message);
    }
}
