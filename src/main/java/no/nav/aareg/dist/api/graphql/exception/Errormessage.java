package no.nav.aareg.dist.api.graphql.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class Errormessage {

    public static final String FIELD_CODE = "code";
    public static final String FIELD_MESSAGE = "message";
    public static final String FIELD_DETAILS = "details";

    private String code;
    private String message;
    private String details;
}
