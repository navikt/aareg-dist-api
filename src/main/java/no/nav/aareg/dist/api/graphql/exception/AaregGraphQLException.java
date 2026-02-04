package no.nav.aareg.dist.api.graphql.exception;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.execution.DataFetcherResult;
import graphql.language.SourceLocation;

import java.util.List;
import java.util.Map;

public abstract class AaregGraphQLException extends RuntimeException implements GraphQLError {

    public static final String FIELD_EXTENSIONS = "extensions";
    public static final String FIELD_ERRORMESSAGES = "errormessages";

    private final Map<String, Object> extensions;

    public AaregGraphQLException(String message, Map<String, Object> extensions) {
        super(message);
        this.extensions = extensions;
    }

    public abstract ErrorClassification getErrorClassification();

    @Override
    public Map<String, Object> getExtensions() {
        return extensions;
    }

    @Override
    public ErrorClassification getErrorType() {
        return getErrorClassification();
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    public <T> DataFetcherResult<T> toDataFetcherResult() {
        return DataFetcherResult.<T>newResult().error(this).build();
    }
}
