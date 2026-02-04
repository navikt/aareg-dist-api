package no.nav.aareg.dist.api.graphql.config;

import graphql.schema.idl.TypeRuntimeWiring;
import no.nav.aareg.dist.api.graphql.data.ArbeidsforholdDataFetcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;
import static no.nav.aareg.dist.api.graphql.common.Queries.QUERY_ARBEIDSFORHOLD;

@Configuration(proxyBeanMethods = false)
public class DataFetcherConfig {

    @Bean
    static TypeRuntimeWiring typeWiring(ArbeidsforholdDataFetcher arbeidsforholdDataFetcher) {
        return newTypeWiring("AaregDistApiQueries")
                .dataFetcher(QUERY_ARBEIDSFORHOLD, arbeidsforholdDataFetcher)
                .build();
    }
}
