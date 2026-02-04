package no.nav.aareg.dist.api.metrics;

public final class MetricsConstants {

    public static final String DB_QUERY_METRIC_NAME = "db.query.timed";
    public static final String DB_QUERY_METRIC_TAG = "query";

    public static final String GRAPHQL_ERROR_METRIC_NAME = "graphql.response.errors.count";
    public static final String GRAPHQL_ERROR_CODE_METRIC_TAG = "code";
    public static final String GRAPHQL_ERROR_QUERY_METRIC_TAG = "query";

    public static final String GRAPHQL_QUERY_METRIC_NAME = "graphql.query.timed";
    public static final String GRAPHQL_QUERY_METRIC_TAG = "query";

    public static final String JMS_QUEUE_METRIC_NAME = "jms.sync.timed";
    public static final String JMS_QUEUE_NAME_METRIC_TAG = "queue";
    public static final String JMS_QUEUE_TYPE_METRIC_TAG = "type";

    public static final String ORG_NUMMER_REQUEST_ATTRIBUTE_NAVN = "aareg-konsument-organisasjon";
    public static final String ORG_NUMMER_METRIC_TAG_NAVN = "organisasjon";
    public static final String DATABEHANDLER_METRIC_TAG_NAVN = "databehandler";

    public static final String AAREG_ARBEIDSFORHOLD_OPPSLAG_NAVN = "aareg_arbeidsforhold_oppslag";

    public static final String OPPSLAGSTYPE_METRIC_TAG_NAVN = "oppslagstype";

    public static final String AAREG_GRAPHQL_OPPSLAG = "aareg_graphql_oppslag";
}
