package no.nav.aareg.dist.api.graphql.coercing;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingSerializeException;
import org.jspecify.annotations.NonNull;

import java.time.LocalDateTime;
import java.util.Locale;

public class DateTimeCoercing implements Coercing<LocalDateTime, String> {

    public static final DateTimeCoercing INSTANCE = new DateTimeCoercing();

    private DateTimeCoercing() {
    }

    @Override
    public String serialize(
            @NonNull Object input,
            @NonNull GraphQLContext graphQLContext,
            @NonNull Locale locale
    ) {
        if (input instanceof LocalDateTime localDateTime) {
            return localDateTime.toString();
        }
        throw new CoercingSerializeException("Serialization from " + input.getClass() + " to DateTime not implemented.");
    }

    @Override
    public LocalDateTime parseValue(
            @NonNull Object input,
            @NonNull GraphQLContext graphQLContext,
            @NonNull Locale locale
    ) {
        throw new CoercingSerializeException("Parsing from " + input.getClass() + " to DateTime not implemented.");
    }

    @Override
    public LocalDateTime parseLiteral(
            @NonNull Value<?> input,
            @NonNull CoercedVariables variables,
            @NonNull GraphQLContext graphQLContext,
            @NonNull Locale locale) {
        throw new CoercingParseLiteralException("Parsing of literal " + input.getClass() + " to DateTime not implemented.");
    }
}
