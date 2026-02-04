package no.nav.aareg.dist.api.graphql.config;

import graphql.GraphQL;
import graphql.schema.GraphQLScalarType;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;
import lombok.SneakyThrows;
import no.nav.aareg.dist.api.graphql.exception.AaregDistApiErrorHandler;
import no.nav.aareg.dist.api.domain.api.v1.ForenkletAnsettelsesdetaljer;
import no.nav.aareg.dist.api.domain.api.v1.FrilanserAnsettelsesdetaljer;
import no.nav.aareg.dist.api.domain.api.v1.MaritimAnsettelsesdetaljer;
import no.nav.aareg.dist.api.domain.api.v1.OrdinaerAnsettelsesdetaljer;
import no.nav.aareg.dist.api.request.AaregRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStreamReader;
import java.util.List;

import static graphql.GraphQL.newGraphQL;
import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

@Configuration(proxyBeanMethods = false)
@Import({
        GraphQLScalarConfig.class,
        AaregRequestFilter.class
})
public class GraphQLConfig {

    @Bean
    @SneakyThrows
    static GraphQL graphQL(List<TypeRuntimeWiring> wirings, List<GraphQLScalarType> scalars, AaregDistApiErrorHandler errorHandler) {
        var queriesStream = new ClassPathResource("schemas/aareg-dist-api-v1.graphqls").getInputStream();
        var runtimeWiring = newRuntimeWiring();

        scalars.forEach(runtimeWiring::scalar);
        wirings.forEach(runtimeWiring::type);

        runtimeWiring.type("Ansettelsesdetaljer", typebuilder -> typebuilder.typeResolver(env -> {
            Object javaObject = env.getObject();
            return switch (javaObject) {
                case OrdinaerAnsettelsesdetaljer ordinaerAnsettelsesdetaljer ->
                        env.getSchema().getObjectType("OrdinaerAnsettelsesdetaljer");
                case MaritimAnsettelsesdetaljer maritimAnsettelsesdetaljer ->
                        env.getSchema().getObjectType("MaritimAnsettelsesdetaljer");
                case FrilanserAnsettelsesdetaljer frilanserAnsettelsesdetaljer ->
                        env.getSchema().getObjectType("FrilanserAnsettelsesdetaljer");
                case ForenkletAnsettelsesdetaljer forenkletAnsettelsesdetaljer -> env.getSchema().getObjectType("ForenkletAnsettelsesdetaljer");
                case null, default -> throw new IllegalStateException("Unexpected value: " + javaObject);
            };
        }));

        var typeRegistry = new TypeDefinitionRegistry().merge(new SchemaParser().parse(new InputStreamReader(queriesStream)));

        var schema = new SchemaGenerator().makeExecutableSchema(typeRegistry, runtimeWiring.build());

        return newGraphQL(schema)
                .defaultDataFetcherExceptionHandler(errorHandler)
                .build();
    }
}