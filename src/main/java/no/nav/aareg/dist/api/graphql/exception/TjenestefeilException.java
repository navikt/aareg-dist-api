package no.nav.aareg.dist.api.graphql.exception;

import graphql.ErrorClassification;

import java.util.Map;

import static graphql.ErrorType.ExecutionAborted;
import static java.util.Collections.singletonList;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistFeilkode.INTERN_FEIL;

public class TjenestefeilException extends AaregGraphQLException {

    public static final String ERRORMESSAGE = "En ukjent feil oppstod";

    public TjenestefeilException() {
        super(ERRORMESSAGE, Map.of(FIELD_ERRORMESSAGES, singletonList(INTERN_FEIL.toErrorMessage())));
    }

    @Override
    public ErrorClassification getErrorClassification() {
        return ExecutionAborted;
    }
}
