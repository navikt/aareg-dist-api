package no.nav.aareg.dist.api.graphql.coercing;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingSerializeException;
import org.jspecify.annotations.NonNull;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class YearMonthCoercing implements Coercing<YearMonth, String> {

    public static final YearMonthCoercing INSTANCE = new YearMonthCoercing();

    private YearMonthCoercing() {
    }

    @Override
    public String serialize(
            @NonNull Object input,
            @NonNull GraphQLContext graphQLContext,
            @NonNull Locale locale
    ) {
        if (input instanceof YearMonth yearMonth) {
            return yearMonth.toString();
        }
        throw new CoercingSerializeException("Serialization from " + input.getClass() + " to DateTime not implemented.");
    }

    @Override
    public YearMonth parseValue(
            @NonNull Object input,
            @NonNull GraphQLContext graphQLContext,
            @NonNull Locale locale
    ) {
        throw new CoercingSerializeException("Parsing from " + input.getClass() + " to Date not implemented.");
    }

    @Override
    public YearMonth parseLiteral(
            @NonNull Value<?> input,
            @NonNull CoercedVariables variables,
            @NonNull GraphQLContext graphQLContext,
            @NonNull Locale locale
    ) {
        if (input instanceof StringValue stringValue) {
            try {
                return YearMonth.parse(stringValue.getValue());
            } catch (DateTimeParseException e) {
                throw new CoercingParseLiteralException("Value not a valid date. Provided value: " + input + ". Expected format: YYYY-MM", e);
            }
        }
        throw new CoercingParseLiteralException("Value not a valid date. Provided value: " + input + ". Expected format: YYYY-MM");
    }
}