package no.nav.aareg.dist.api.graphql.config;

import graphql.schema.GraphQLScalarType;
import no.nav.aareg.dist.api.graphql.coercing.DateCoercing;
import no.nav.aareg.dist.api.graphql.coercing.DateTimeCoercing;
import no.nav.aareg.dist.api.graphql.coercing.YearMonthCoercing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class GraphQLScalarConfig {

    @Bean
    GraphQLScalarType dateScalar() {
        return GraphQLScalarType.newScalar()
                .name("Date")
                .description("Format: YYYY-MM-DD, example: 2011-12-03")
                .coercing(DateCoercing.INSTANCE)
                .build();
    }

    @Bean
    GraphQLScalarType dateTimeScalar() {
        return GraphQLScalarType.newScalar()
                .name("DateTime")
                .description("Format: YYYY-MM-DDTHH:mm:SS (ISO-8601), example: 2011-12-03T10:15:30")
                .coercing(DateTimeCoercing.INSTANCE)
                .build();
    }

    @Bean
    GraphQLScalarType yearMonthScalar() {
        return GraphQLScalarType.newScalar()
                .name("YearMonth")
                .description("Format: YYYY-MM (ISO-8601), example: 2018-05")
                .coercing(YearMonthCoercing.INSTANCE)
                .build();
    }
}