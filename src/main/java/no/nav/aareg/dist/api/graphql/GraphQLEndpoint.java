package no.nav.aareg.dist.api.graphql;

import graphql.ExceptionWhileDataFetching;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.execution.ResultPath;
import graphql.language.SourceLocation;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.aareg.dist.api.config.security.MaskinportenClaimsService;
import no.nav.aareg.dist.api.graphql.common.AaregExecutionResult;
import no.nav.aareg.dist.api.graphql.exception.AaregDistApiErrorHandler;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

import static graphql.ExecutionInput.newExecutionInput;
import static graphql.ExecutionResultImpl.newExecutionResult;
import static java.util.Optional.ofNullable;
import static no.nav.aareg.dist.api.config.security.MaskinportenClaimsService.CONSUMER;
import static no.nav.aareg.dist.api.config.security.MaskinportenClaimsService.SUPPLIER;
import static no.nav.aareg.dist.api.domain.MDCConstants.CONSUMER_ID;
import static no.nav.aareg.dist.api.domain.MDCConstants.COUNT;
import static no.nav.aareg.dist.api.domain.MDCConstants.ELAPSED_TIME;
import static no.nav.aareg.dist.api.domain.MDCConstants.QUERY_NAME;
import static no.nav.aareg.dist.api.domain.MDCConstants.SUPPLIER_ID;
import static no.nav.aareg.dist.api.graphql.utils.GraphQLUtil.appendInlineVariablesFromRequestObject;
import static no.nav.aareg.dist.api.graphql.utils.GraphQLUtil.getQueryName;
import static no.nav.aareg.dist.api.graphql.utils.GraphQLUtil.toJson;
import static no.nav.aareg.dist.api.metrics.MetricsConstants.AAREG_ARBEIDSFORHOLD_OPPSLAG_NAVN;
import static no.nav.aareg.dist.api.metrics.MetricsConstants.AAREG_GRAPHQL_OPPSLAG;
import static no.nav.aareg.dist.api.metrics.MetricsConstants.DATABEHANDLER_METRIC_TAG_NAVN;
import static no.nav.aareg.dist.api.metrics.MetricsConstants.OPPSLAGSTYPE_METRIC_TAG_NAVN;
import static no.nav.aareg.dist.api.metrics.MetricsConstants.ORG_NUMMER_METRIC_TAG_NAVN;
import static no.nav.aareg.dist.api.request.RequestAttributes.ATTRIBUTE_AAREG_KONSUMENT_DATABEHANDLER_ORGNUMMER;
import static no.nav.aareg.dist.api.request.RequestAttributes.ATTRIBUTE_AAREG_KONSUMENT_OPERATION;
import static no.nav.aareg.dist.api.request.RequestAttributes.ATTRIBUTE_AAREG_KONSUMENT_ORGNUMMER;
import static no.nav.aareg.dist.api.request.RequestHeaders.CORRELATION_ID;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GraphQLEndpoint {

    private static final Marker TEAM_LOGS_MARKER = MarkerFactory.getMarker("TEAM_LOGS");

    private final GraphQL graphQL;
    private final MaskinportenClaimsService maskinportenClaimsService;
    private final MeterRegistry meterRegistry;
    private final AaregDistApiErrorHandler aaregDistApiErrorHandler;


    @PostMapping(path = "/graphql", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> graphql(HttpServletRequest httpServletRequest, @RequestBody GraphQLRequest request) {
        var stopWatch = new StopWatch();
        stopWatch.start();

        appendInlineVariablesFromRequestObject(request);

        ExecutionResult executionResult = newExecutionResult().build();
        AaregExecutionResult aaregExecutionResult;

        var queryName = getQueryName(request);
        var organisasjon = "";
        String databehandler = null;

        try {
            organisasjon = maskinportenClaimsService.hentOrgnrFraToken().get(CONSUMER);
            databehandler = maskinportenClaimsService.hentOrgnrFraToken().get(SUPPLIER);

            MDC.put(QUERY_NAME, queryName);
            MDC.put(CONSUMER_ID, organisasjon);
            MDC.put(SUPPLIER_ID, databehandler);

            httpServletRequest.setAttribute(ATTRIBUTE_AAREG_KONSUMENT_ORGNUMMER, organisasjon);
            httpServletRequest.setAttribute(ATTRIBUTE_AAREG_KONSUMENT_DATABEHANDLER_ORGNUMMER, databehandler);
            httpServletRequest.setAttribute(ATTRIBUTE_AAREG_KONSUMENT_OPERATION, queryName);

            var executionInput = newExecutionInput()
                    .query(request.getQuery())
                    .variables(request.getVariables())
                    .build();

            executionResult = graphQL.execute(executionInput);
        } catch (Exception e) {
            log.error(TEAM_LOGS_MARKER, e.getMessage(), e);
            executionResult = newExecutionResult()
                    .addError(new ExceptionWhileDataFetching(ResultPath.parse("/" + queryName), e, SourceLocation.EMPTY))
                    .build();
        } finally {
            if (executionResult != null && !executionResult.getErrors().isEmpty()) {
                var updatedErrors = aaregDistApiErrorHandler.processErrors(executionResult.getErrors());
                var updatedExecutionResult = newExecutionResult()
                        .errors(updatedErrors)
                        .build();
                aaregExecutionResult = new AaregExecutionResult(queryName, updatedExecutionResult);
            } else {
                aaregExecutionResult = new AaregExecutionResult(queryName, executionResult);
            }
            log(request, executionResult, organisasjon, databehandler, stopWatch);
        }

        return ResponseEntity.ok().body(aaregExecutionResult.toSpecification());
    }

    private void log(
            GraphQLRequest request,
            ExecutionResult executionResult,
            String organisasjon,
            String databehandler,
            StopWatch stopWatch
    ) {
        var antall = executionResult.getData() != null ? ((LinkedHashMap) executionResult.getData()).size() : -1;
        var behandlingstid = String.valueOf(stopWatch.getTotalTimeNanos() / 1_000_000);
        var databehandlerNonNull = ofNullable(databehandler).orElse("");

        MDC.put(COUNT, String.valueOf(antall));
        MDC.put(ELAPSED_TIME, behandlingstid);

        var queryName = getQueryName(request);
        var correlationId = MDC.get(CORRELATION_ID);
        var variablesAsJson = toJson(request.getVariables());

        log.info(TEAM_LOGS_MARKER, "{}({}):\n{}", queryName, correlationId, variablesAsJson);

        if (antall >= 0) {
            meterRegistry.counter(AAREG_GRAPHQL_OPPSLAG,
                    OPPSLAGSTYPE_METRIC_TAG_NAVN, queryName,
                    ORG_NUMMER_METRIC_TAG_NAVN, organisasjon,
                    DATABEHANDLER_METRIC_TAG_NAVN, databehandlerNonNull).increment();
            meterRegistry.counter(AAREG_ARBEIDSFORHOLD_OPPSLAG_NAVN,
                    OPPSLAGSTYPE_METRIC_TAG_NAVN, queryName,
                    ORG_NUMMER_METRIC_TAG_NAVN, organisasjon,
                    DATABEHANDLER_METRIC_TAG_NAVN, databehandlerNonNull).increment(antall);
            log.info("{}({}): Hentet {} dokument(er) på {} ms", queryName, correlationId, antall, behandlingstid);
        }
    }
}