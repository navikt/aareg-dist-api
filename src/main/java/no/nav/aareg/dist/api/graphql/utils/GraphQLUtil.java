package no.nav.aareg.dist.api.graphql.utils;

import graphql.schema.DataFetchingEnvironment;
import no.nav.aareg.dist.api.graphql.GraphQLRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static graphql.language.AstPrinter.printAst;
import static graphql.language.AstPrinter.printAstCompact;
import static java.lang.String.join;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static org.springframework.util.StringUtils.split;

public final class GraphQLUtil {

    public static final String QUERYNAME_INTROSPECTION = "introspection";

    private static final String INTROSPECTION_SCHEMA = "__schema";
    private static final String INTROSPECTION_TYPE = "__type";

    private GraphQLUtil() {
    }

    public static GraphQLRequest toGraphQLRequest(DataFetchingEnvironment dataFetchingEnvironment, boolean compactAst) {
        var query = compactAst ? printAstCompact(dataFetchingEnvironment.getDocument()) : printAst(dataFetchingEnvironment.getDocument());
        if (!query.contains("query")) { // ved inline query trimmers query vekk fra starten
            query = "query" + query + "}";
        }
        return GraphQLRequest.builder()
                .query(query)
                .variables(dataFetchingEnvironment.getArguments())
                .build();
    }

    public static String getQueryName(GraphQLRequest request) {
        return request.getQuery().contains(INTROSPECTION_SCHEMA) || request.getQuery().contains(INTROSPECTION_TYPE)
                ? QUERYNAME_INTROSPECTION
                : getGraphQLQueryName(request.getQuery());
    }

    public static void appendInlineVariablesFromRequestObject(GraphQLRequest request) {
        var inlineVariables = getInlineVariablesFromRequestObject(request.getQuery());
        if (request.getVariables() == null) {
            request.setVariables(inlineVariables);
        } else {
            request.getVariables().putAll(inlineVariables);
        }
    }

    public static void appendInlineVariables(GraphQLRequest request) {
        var inlineVariables = getInlineVariables(request.getQuery());
        if (request.getVariables() == null) {
            request.setVariables(inlineVariables);
        } else {
            request.getVariables().putAll(inlineVariables);
        }
    }

    public static String toCompactJson(Map<String, Object> variables) {
        return toJson(variables).replaceAll("[\\n\\t\\r ]", "");
    }

    public static String toJson(Map<String, Object> variables) {
        return "{\n" +
                variables.entrySet().stream()
                        .map(entry -> "\t\"" + entry.getKey() + "\": " + getJsonVariableValue(entry.getValue()))
                        .collect(joining(",\n")) +
                "\n}";
    }

    @SuppressWarnings("unchecked")
    private static String getJsonVariableValue(Object value) {
        if (value != null) {
            switch (value) {
            case String s -> {
                return "\"" + value + "\"";
            }
            case Boolean b -> {
                return value.toString();
            }
            case Enum anEnum -> {
                return "\"" + anEnum.name() + "\"";
            }
            case Collection collection -> {
                var collectionItems = new ArrayList<String>();
                collection.forEach(item -> collectionItems.add(getJsonVariableValue(item)));
                return "[" + join(",", collectionItems) + "]";
            }
            case Map map -> {
                return "{\n" +
                        ((Map<String, Object>) value).entrySet().stream()
                                .map(entry -> "\t\"" + entry.getKey() + "\": " + getJsonVariableValue(entry.getValue()))
                                .collect(joining(",\n")) +
                        "\n}";
            }
            case null, default -> {
                return "\"Unsupported Type\"";
            }
            }
        }
        return "null";
    }

    private static Map<String, Object> getInlineVariablesFromRequestObject(String graphqlQuery) {
        var inlineVariables = new LinkedHashMap<String, Object>();
        var nestedInlineVariables = new LinkedHashMap<String, Object>();

        var graphqlQueryArguments = getGraphQLQueryArgumentsFromRequestObject(graphqlQuery);

        var requestObjectWithVariables = graphqlQueryArguments.split("[{]");
        if (requestObjectWithVariables.length > 1) {
            var nestedArgumentsRaw = requestObjectWithVariables[1].replace("}", "");
            stream(nestedArgumentsRaw.split(",")).forEach(argument -> {
                if (!argument.contains("$") && !argument.contains("{") && !argument.contains("}")) {
                    var argumentItems = split(argument, ":");
                    if (argumentItems.length > 1 && argumentItems[1].contains("[")) {
                        var trimmedArgs = argumentItems[1].replace("[", "").replace("]", "");
                        var splittedArguments = trimmedArgs.split(",");
                        var argumentList = new ArrayList<>(stream(splittedArguments).toList());
                        nestedInlineVariables.put(argumentItems[0], argumentList);
                    } else if (argumentItems.length > 1 && (argumentItems[1].equalsIgnoreCase("true") || argumentItems[1].equalsIgnoreCase("false"))) {
                        nestedInlineVariables.put(argumentItems[0], Boolean.valueOf(argumentItems[1]));
                    } else {
                        nestedInlineVariables.put(argumentItems[0], argumentItems[1].replace("\"", ""));
                    }
                }
            });

            if (!nestedInlineVariables.isEmpty()) {
                inlineVariables.put(requestObjectWithVariables[0].replace(":", ""), nestedInlineVariables);
            }
        }


        return inlineVariables;
    }

    private static Map<String, Object> getInlineVariables(String graphqlQuery) {
        var inlineVariables = new LinkedHashMap<String, Object>();

        var graphqlQueryArguments = getGraphQLQueryArguments(graphqlQuery);

        stream(split(graphqlQueryArguments, ",")).forEach(argument -> {
            if (!argument.contains("$") && !argument.contains("{") && !argument.contains("}")) {
                var argumentItems = split(argument, ":");
                inlineVariables.put(argumentItems[0], argumentItems[1].replace("\"", ""));
            }
        });

        return inlineVariables;
    }

    private static String getGraphQLQuerySignature(String graphqlQuery) {
        var startIndex = graphqlQuery.indexOf("{") + 1;
        var endIndex = graphqlQuery.indexOf("{", startIndex);

        return graphqlQuery.substring(startIndex, endIndex).replaceAll("[\\n\\t\\r ]", "");
    }

    private static String getGraphQLQuerySignatureWithVariableObject(String graphqlQuery) {
        var queryStartIndex = graphqlQuery.indexOf("{") + 1;
        var variableObjectStartIndex = graphqlQuery.indexOf("{", queryStartIndex);
        var variableObjectEndIndex = graphqlQuery.indexOf("{", variableObjectStartIndex + 1);

        return graphqlQuery.substring(queryStartIndex, variableObjectEndIndex).replaceAll("[\\n\\t\\r ]", "");
    }

    private static String getGraphQLQueryName(String graphqlQuery) {
        var graphqlQuerySignature = getGraphQLQuerySignature(graphqlQuery);
        return graphqlQuerySignature.substring(0, graphqlQuerySignature.indexOf("("));
    }


    private static String getGraphQLQueryArgumentsFromRequestObject(String graphqlQuery) {
        var graphqlQuerySignature = getGraphQLQuerySignatureWithVariableObject(graphqlQuery);
        return !graphqlQuerySignature.equals(INTROSPECTION_SCHEMA)
                ? graphqlQuerySignature.substring(graphqlQuerySignature.indexOf("(") + 1, graphqlQuerySignature.indexOf(")"))
                : "";
    }

    private static String getGraphQLQueryArguments(String graphqlQuery) {
        var graphqlQuerySignature = getGraphQLQuerySignature(graphqlQuery);
        return !graphqlQuerySignature.equals(INTROSPECTION_SCHEMA)
                ? graphqlQuerySignature.substring(graphqlQuerySignature.indexOf("(") + 1, graphqlQuerySignature.indexOf(")"))
                : "";
    }
}
