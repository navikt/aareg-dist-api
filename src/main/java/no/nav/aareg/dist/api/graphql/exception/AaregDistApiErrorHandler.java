package no.nav.aareg.dist.api.graphql.exception;

import graphql.ExceptionWhileDataFetching;
import graphql.GraphQLError;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.InputMapDefinesTooManyFieldsException;
import graphql.execution.NonNullableFieldWasNullError;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static graphql.ErrorType.ExecutionAborted;
import static graphql.ErrorType.ValidationError;
import static graphql.GraphqlErrorBuilder.newError;
import static java.util.Arrays.stream;
import static no.nav.aareg.dist.api.domain.FinnArbeidsforholdVariabler.FIELD_ARBEIDSFORHOLDSTATUS;
import static no.nav.aareg.dist.api.domain.FinnArbeidsforholdVariabler.FIELD_ARBEIDSFORHOLDTYPE;
import static no.nav.aareg.dist.api.domain.FinnArbeidsforholdVariabler.FIELD_RAPPORTERINGSORDNING;
import static no.nav.aareg.dist.api.domain.MDCConstants.QUERY_NAME;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistApiFeilkode.UGYLDIG_DATA;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistApiFeilkode.UGYLDIG_VARIABEL_VERDI;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistErrorMessage.ERRORMESSAGE_INTERN_FEIL;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistErrorMessage.ERRORMESSAGE_UGYLDIG_DATA;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistFeilkode.INTERN_FEIL;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistFeilkode.UGYLDIG_SPOERRING;
import static no.nav.aareg.dist.api.graphql.exception.AaregGraphQLException.FIELD_ERRORMESSAGES;
import static no.nav.aareg.dist.api.graphql.exception.Errormessage.FIELD_CODE;
import static no.nav.aareg.dist.api.graphql.exception.Errormessage.FIELD_DETAILS;
import static no.nav.aareg.dist.api.graphql.exception.Errormessage.FIELD_MESSAGE;
import static no.nav.aareg.dist.api.request.RequestHeaders.CORRELATION_ID;

@Slf4j
@Component
public class AaregDistApiErrorHandler implements DataFetcherExceptionHandler {

    private static final Marker TEAM_LOGS_MARKER = MarkerFactory.getMarker("TEAM_LOGS");

    private static final String FEILMELDING_TEMPLATE = "{}({}): En feil har inntruffet [{}|{}]";
    private static final String UGYLDIG_DATA_TEMPLATE = "{}({}): " + ERRORMESSAGE_UGYLDIG_DATA + " [{}|{}], se secure-logg for detaljer";
    private static final String INTERN_FEIL_TEMPLATE = "{}({}): " + ERRORMESSAGE_INTERN_FEIL + " [{}|{}], se secure-logg for detaljer";

    public List<GraphQLError> processErrors(List<GraphQLError> errors) {
        var outputErrors = new ArrayList<GraphQLError>();

        var queryName = MDC.get(QUERY_NAME);
        var correlationId = MDC.get(CORRELATION_ID);

        errors.forEach(error -> {
            GraphQLError outputError;

            var sourceError = error instanceof ExceptionWhileDataFetching exceptionWhileDataFetching ? exceptionWhileDataFetching.getException() : error;

            if (sourceError instanceof TjenestefeilException) {
                var errormessages = getErrormessages(INTERN_FEIL);
                outputError = newError()
                        .message(ERRORMESSAGE_INTERN_FEIL)
                        .errorType(ExecutionAborted)
                        .extensions(Map.of(FIELD_ERRORMESSAGES, errormessages))
                        .build();
                log.error(FEILMELDING_TEMPLATE, queryName, correlationId, outputError.getErrorType(), sourceError.getClass().getSimpleName());
            } else if (sourceError instanceof AaregGraphQLException aaregGraphQLException) {
                outputError = newError()
                        .message(aaregGraphQLException.getMessage())
                        .errorType(aaregGraphQLException.getErrorType())
                        .extensions(aaregGraphQLException.getExtensions())
                        .build();
                log.info(FEILMELDING_TEMPLATE, queryName, correlationId, outputError.getErrorType(), sourceError.getClass().getSimpleName());
            } else if ((sourceError instanceof CoercingParseValueException)
                    || (sourceError instanceof CoercingParseLiteralException)) {
                var detaljerOverride = detaljerOverride(error.getMessage(), FIELD_ARBEIDSFORHOLDTYPE, FIELD_RAPPORTERINGSORDNING, FIELD_ARBEIDSFORHOLDSTATUS);
                var errormessages = getErrormessages(UGYLDIG_VARIABEL_VERDI, detaljerOverride);
                outputError = newError()
                        .message(UgyldigInput.ERRORMESSAGE)
                        .errorType(ValidationError)
                        .extensions(Map.of(FIELD_ERRORMESSAGES, List.of(errormessages)))
                        .build();
                log.info(FEILMELDING_TEMPLATE, queryName, correlationId, outputError.getErrorType(), sourceError.getClass().getSimpleName());
            } else if ((sourceError instanceof InputMapDefinesTooManyFieldsException) ||
                    (sourceError instanceof graphql.validation.ValidationError)) {
                var errormessages = getErrormessages(UGYLDIG_SPOERRING);
                outputError = newError()
                        .message(UgyldigInput.ERRORMESSAGE)
                        .errorType(ValidationError)
                        .extensions(Map.of(FIELD_ERRORMESSAGES, List.of(errormessages)))
                        .build();
                log.info(FEILMELDING_TEMPLATE, queryName, correlationId, outputError.getErrorType(), sourceError.getClass().getSimpleName());
            } else if ((sourceError instanceof NonNullableFieldWasNullError)) {
                var errormessages = getErrormessages(UGYLDIG_DATA);
                outputError = newError()
                        .message(ERRORMESSAGE_UGYLDIG_DATA)
                        .errorType(ExecutionAborted)
                        .extensions(Map.of(FIELD_ERRORMESSAGES, List.of(errormessages)))
                        .build();
                log.warn(UGYLDIG_DATA_TEMPLATE, queryName, correlationId, outputError.getErrorType(), outputError.getClass().getSimpleName());
                log.warn(TEAM_LOGS_MARKER, UGYLDIG_DATA_TEMPLATE + "\n{}", queryName, correlationId, error.getErrorType(), sourceError.getClass().getSimpleName(), error.toSpecification().toString());
            } else {
                var errormessages = getErrormessages(INTERN_FEIL);
                outputError = newError()
                        .message(ERRORMESSAGE_INTERN_FEIL)
                        .errorType(ExecutionAborted)
                        .extensions(Map.of(FIELD_ERRORMESSAGES, errormessages))
                        .build();
                log.error(INTERN_FEIL_TEMPLATE, queryName, correlationId, outputError.getErrorType(), outputError.getClass().getSimpleName());
                log.error(TEAM_LOGS_MARKER, INTERN_FEIL_TEMPLATE + "\n{}", queryName, correlationId, error.getErrorType(), sourceError.getClass().getSimpleName(), error.toSpecification().toString());
            }

            outputErrors.add(outputError);
        });

        return outputErrors;
    }

    private Optional<String> detaljerOverride(String errorMessage, String... feltnavn) {
        return stream(feltnavn).filter(errorMessage::contains).findFirst();
    }

    private Map<String, String> getErrormessages(AaregDistFeilkode feilkode) {
        return getErrormessages(feilkode, Optional.empty());
    }

    private Map<String, String> getErrormessages(AaregDistFeilkode feilkode, Optional<String> detaljerOverride) {
        var errormessages = new LinkedHashMap<String, String>();
        errormessages.put(FIELD_CODE, feilkode.getKode());
        errormessages.put(FIELD_MESSAGE, feilkode.getBeskrivelse());
        errormessages.put(FIELD_DETAILS, detaljerOverride.orElse(feilkode.getDetaljer()));
        return errormessages;
    }

    @Override
    public CompletableFuture<DataFetcherExceptionHandlerResult> handleException(DataFetcherExceptionHandlerParameters handlerParameters) {
        var thrownException = handlerParameters.getException();

        var queryName = MDC.get(QUERY_NAME);
        var correlationId = MDC.get(CORRELATION_ID);

        GraphQLError outputError;


        if (thrownException instanceof AaregGraphQLException aaregGraphQLException) {
            outputError = newError()
                    .message(aaregGraphQLException.getMessage())
                    .errorType(aaregGraphQLException.getErrorType())
                    .extensions(aaregGraphQLException.getExtensions())
                    .build();
            log.info(FEILMELDING_TEMPLATE, queryName, correlationId, outputError.getErrorType(), thrownException.getClass().getSimpleName());
        } else if ((thrownException instanceof graphql.schema.CoercingSerializeException)
                || (thrownException instanceof graphql.schema.CoercingParseValueException)
                || (thrownException instanceof graphql.execution.UnresolvedTypeException)) {
            var detaljerOverride = detaljerOverride(thrownException.getMessage(), FIELD_ARBEIDSFORHOLDTYPE, FIELD_RAPPORTERINGSORDNING, FIELD_ARBEIDSFORHOLDSTATUS);
            var errormessages = getErrormessages(UGYLDIG_VARIABEL_VERDI, detaljerOverride);
            outputError = newError()
                    .message(UgyldigInput.ERRORMESSAGE)
                    .errorType(ValidationError)
                    .extensions(Map.of(FIELD_ERRORMESSAGES, List.of(errormessages)))
                    .build();
            log.info(FEILMELDING_TEMPLATE, queryName, correlationId, outputError.getErrorType(), thrownException.getClass().getSimpleName());
        } else if ((thrownException instanceof graphql.execution.InputMapDefinesTooManyFieldsException)) {
            var errormessages = getErrormessages(UGYLDIG_SPOERRING);
            outputError = newError()
                    .message(UgyldigInput.ERRORMESSAGE)
                    .errorType(ValidationError)
                    .extensions(Map.of(FIELD_ERRORMESSAGES, List.of(errormessages)))
                    .build();
            log.info(FEILMELDING_TEMPLATE, queryName, correlationId, outputError.getErrorType(), thrownException.getClass().getSimpleName());
        } else if ((thrownException instanceof graphql.execution.NonNullableValueCoercedAsNullException)) {
            var errormessages = getErrormessages(UGYLDIG_DATA);
            outputError = newError()
                    .message(ERRORMESSAGE_UGYLDIG_DATA)
                    .errorType(ExecutionAborted)
                    .extensions(Map.of(FIELD_ERRORMESSAGES, List.of(errormessages)))
                    .build();
            log.warn(UGYLDIG_DATA_TEMPLATE, queryName, correlationId, outputError.getErrorType(), outputError.getClass().getSimpleName());
            log.warn(TEAM_LOGS_MARKER, UGYLDIG_DATA_TEMPLATE + "\n{}", queryName, correlationId, outputError.getErrorType(), thrownException.getClass().getSimpleName(), outputError.toSpecification().toString());
        } else {
            var errormessages = getErrormessages(INTERN_FEIL);
            outputError = newError()
                    .message(ERRORMESSAGE_INTERN_FEIL)
                    .errorType(ExecutionAborted)
                    .extensions(Map.of(FIELD_ERRORMESSAGES, errormessages))
                    .build();
            log.error(INTERN_FEIL_TEMPLATE, queryName, correlationId, outputError.getErrorType(), outputError.getClass().getSimpleName());
            log.error(TEAM_LOGS_MARKER, INTERN_FEIL_TEMPLATE + "\n{}", queryName, correlationId, outputError.getErrorType(), thrownException.getClass().getSimpleName(), outputError.toSpecification().toString());
        }

        var result = DataFetcherExceptionHandlerResult.newResult()
                .error(outputError)
                .build();

        return CompletableFuture.completedFuture(result);
    }
}
