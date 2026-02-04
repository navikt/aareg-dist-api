package no.nav.aareg.dist.api.graphql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class GraphQLRequest {

    private String query;

    private Map<String, Object> variables;
}