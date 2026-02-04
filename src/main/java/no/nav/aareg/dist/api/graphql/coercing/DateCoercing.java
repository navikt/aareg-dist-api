package no.nav.aareg.dist.api.graphql.coercing;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingSerializeException;
import org.jspecify.annotations.NonNull;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class DateCoercing implements Coercing<LocalDate, String> {

    public static final DateCoercing INSTANCE = new DateCoercing();

    private DateCoercing() {
    }

    @Override
    public String serialize(
            @NonNull Object input,
            @NonNull GraphQLContext graphQLContext,
            @NonNull Locale locale
    ) {
        if (input instanceof LocalDate) {
            return input.toString();
        }
        throw new CoercingSerializeException("Serialization from " + input.getClass() + " to Date not implemented.");
    }

    @Override
    public LocalDate parseValue(
            @NonNull Object input,
            @NonNull GraphQLContext graphQLContext,
            @NonNull Locale locale
    ) {
        throw new CoercingSerializeException("Parsing from " + input.getClass() + " to Date not implemented.");
    }

    @Override
    public LocalDate parseLiteral(
            @NonNull Value<?> input,
            @NonNull CoercedVariables variables,
            @NonNull GraphQLContext graphQLContext,
            @NonNull Locale locale
    ) {
        if (input instanceof StringValue stringValue) {
            try {
                return LocalDate.parse(stringValue.getValue());
            } catch (DateTimeParseException e) {
                throw new CoercingParseLiteralException("Value not a valid date. Provided value: " + input + ". Expected format: YYYY-MM-DD", e);
            }
        }
        throw new CoercingParseLiteralException("Value not a valid date. Provided value: " + input + ". Expected format: YYYY-MM-DD");
    }
}
