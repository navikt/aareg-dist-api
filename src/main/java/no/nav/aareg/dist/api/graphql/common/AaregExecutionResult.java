package no.nav.aareg.dist.api.graphql.common;

import graphql.ExecutionResult;
import graphql.ExecutionResultImpl;
import lombok.Getter;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static no.nav.aareg.dist.api.graphql.common.Queries.getDataRootElement;

@SuppressWarnings("unchecked")
@Getter
public class AaregExecutionResult extends ExecutionResultImpl {

    private static final String SPECIFICATION_DATA = "data";

    private final String queryName;

    public AaregExecutionResult(String queryName, ExecutionResult executionResult) {
        super(executionResult.getData(), executionResult.getErrors());
        this.queryName = queryName;
    }

    @Override
    public Map<String, Object> toSpecification() {
        var specification = super.toSpecification();

        if (!getErrors().isEmpty()) {
            specification.remove(SPECIFICATION_DATA);
        }

        return specification;
    }

    @Override
    public List<Map<String, Object>> getData() {
        var specification = toSpecification();
        var data = isDataPresent() && specification.get(SPECIFICATION_DATA) != null ? (Map<String, Object>) specification.get(SPECIFICATION_DATA) : emptyMap();
        var dataRootElement = getDataRootElement(queryName);
        var queryData = data.get(queryName) != null ? (Map<String, Object>) data.get(queryName) : emptyMap();
        return queryData.get(dataRootElement) != null ? (List<Map<String, Object>>) queryData.get(dataRootElement) : emptyList();
    }
}
