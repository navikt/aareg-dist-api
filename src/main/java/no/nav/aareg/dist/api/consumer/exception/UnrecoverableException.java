package no.nav.aareg.dist.api.consumer.exception;

public class UnrecoverableException extends RuntimeException {

    public UnrecoverableException(String message) {
        super(message);
    }

    public UnrecoverableException(Throwable cause) {
        super(cause);
    }

    public UnrecoverableException(String message, Throwable cause) {
        super(message, cause);
    }
}
