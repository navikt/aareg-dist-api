package no.nav.aareg.dist.api.graphql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.util.Collections;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphQLRequest {

    private String query;

    @JsonDeserialize(using = VariablesDeserializer.class)
    private Map<String, Object> variables;

    static class VariablesDeserializer extends ValueDeserializer<Map<String, Object>> {
        private static final ObjectMapper MAPPER = new ObjectMapper();

        @Override
        @SuppressWarnings("unchecked")
        public Map<String, Object> deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
            if (p.currentToken() == JsonToken.VALUE_STRING) {
                String text = p.getText();
                if (text == null || text.isBlank()) {
                    return Collections.emptyMap();
                }
                return MAPPER.readValue(text, Map.class);
            }
            if (p.currentToken() == JsonToken.VALUE_NULL) {
                return null;
            }
            return p.readValueAs(Map.class);
        }
    }
}