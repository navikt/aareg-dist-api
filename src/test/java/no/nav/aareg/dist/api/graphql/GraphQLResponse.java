package no.nav.aareg.dist.api.graphql;

import tools.jackson.databind.JsonNode;
import no.nav.aareg.dist.api.domain.api.v1.Arbeidsforholdliste;

import java.util.List;
import java.util.Map;

public record GraphQLResponse(no.nav.aareg.dist.api.graphql.GraphQLResponse.Data data, List<JsonNode> errors, Map<String, Object> extensions) {

    public record Data(Arbeidsforholdliste finnArbeidsforhold) {

    }
}
