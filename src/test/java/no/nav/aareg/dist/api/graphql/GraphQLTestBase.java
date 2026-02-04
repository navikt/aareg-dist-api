package no.nav.aareg.dist.api.graphql;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import lombok.Setter;
import lombok.SneakyThrows;
import no.nav.aareg.dist.api.DistApiTest;
import no.nav.aareg.dist.api.graphql.exception.AaregDistFeilkode;
import no.nav.aareg.dist.api.service.ArbeidsforholdDataService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.util.StringUtils;
import tools.jackson.databind.JsonNode;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static java.nio.file.Files.readAllBytes;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static no.nav.aareg.dist.api.graphql.exception.AaregGraphQLException.FIELD_ERRORMESSAGES;
import static no.nav.aareg.dist.api.graphql.exception.AaregGraphQLException.FIELD_EXTENSIONS;
import static no.nav.aareg.dist.api.graphql.exception.Errormessage.FIELD_CODE;
import static no.nav.aareg.dist.api.graphql.exception.Errormessage.FIELD_DETAILS;
import static no.nav.aareg.dist.api.graphql.exception.Errormessage.FIELD_MESSAGE;
import static no.nav.aareg.dist.api.graphql.utils.AaregCollectors.toSingleton;
import static no.nav.aareg.dist.api.request.RequestHeaders.CORRELATION_ID;
import static no.nav.aareg.dist.api.util.LoggerMockingUtil.getMockedAuditLogAppender;
import static no.nav.aareg.dist.api.util.LoggerMockingUtil.getMockedSystemLogAppender;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.slf4j.event.Level.WARN;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.util.CollectionUtils.isEmpty;


public abstract class GraphQLTestBase extends DistApiTest {

    private static final String SCOPE = "nav:aareg/v1/arbeidsforhold";

    private static final String ARBEIDSFORHOLD_ACCESS_LOGGING_INFO_EVENT_START = "CEF:0|Aareg|aareg-dist-api|1.0|audit:access|Arbeidsforhold|INFO|flexString1Label=Decision flexString1=permit";
    private static final String ARBEIDSFORHOLD_ACCESS_LOGGING_WARN_EVENT_START = "CEF:0|Aareg|aareg-dist-api|1.0|audit:access|Arbeidsforhold|WARN|flexString1Label=Decision flexString1=deny";

    private static final String MASKINPORTEN_CONSUMER = "815493000";
    private static final String MASKINPORTEN_SUPPLIER = "815493001";

    protected Appender<ILoggingEvent> mockedAuditLogAppender;
    protected Appender<ILoggingEvent> mockedSystemLogAppender;

    protected final ArgumentCaptor<ILoggingEvent> auditLogEventArgumentCaptor = ArgumentCaptor.forClass(ILoggingEvent.class);

    protected final ArgumentCaptor<ILoggingEvent> systemLogEventArgumentCaptor = ArgumentCaptor.forClass(ILoggingEvent.class);

    @Autowired
    protected ArbeidsforholdDataService arbeidsforholdDataService;

    @Autowired
    private RestTestClient restTestClient;

    private boolean addAuthorization = true;
    private boolean useCustomClaims = false;

    private String customToken = null;

    @Setter
    private HttpStatus expectedHttpStatus = HttpStatus.OK;

    protected static Set<String> getExpectedErrormessageCodes(AaregDistFeilkode... feilkode) {
        var errormessageCodes = new LinkedHashSet<String>();
        stream(feilkode).forEach(kode -> errormessageCodes.add("\"" + kode.getKode() + "\""));
        return errormessageCodes;
    }

    protected static Set<String> getExpectedErrormessageDetails(AaregDistFeilkode... feilkode) {
        var errormessageDetails = new LinkedHashSet<String>();
        stream(feilkode).forEach(kode -> errormessageDetails.add(StringUtils.hasText(kode.getDetaljer()) ? "\"" + kode.getDetaljer() + "\"" : "null"));
        return errormessageDetails;
    }

    protected static void assertDataPresent(GraphQLResponse response) {
        assertAll(
                () -> assertNotNull(response.data()),
                () -> assertNotNull(response.data().finnArbeidsforhold()),
                () -> assertNotNull(response.data().finnArbeidsforhold().getArbeidsforhold())
        );
    }

    protected static void assertDataCount(int expectedCount, GraphQLResponse response) {
        assertAll(
                () -> assertDataPresent(response),
                () -> assertEquals(expectedCount, response.data().finnArbeidsforhold().getArbeidsforhold().size())
        );
    }

    protected static void assertNoData(GraphQLResponse response) {
        assertTrue(
                response.data() == null
                        || response.data().finnArbeidsforhold() == null
                        || isEmpty(response.data().finnArbeidsforhold().getArbeidsforhold())
        );
    }

    protected static void assertDataExists(GraphQLResponse response) {
        assertAll(
                () -> assertNotNull(response.data()),
                () -> assertNotNull(response.data().finnArbeidsforhold()),
                () -> assertFalse(response.data().finnArbeidsforhold().getArbeidsforhold().isEmpty())
        );
    }

    protected static void assertNoErrors(GraphQLResponse response) {
        assertNull(response.errors());
    }

    protected static void assertErrorMessage(String expectedErrorMessage, GraphQLResponse response) {
        assertAll(
                () -> assertSingleError(response),
                () -> assertErrorMessages(List.of(expectedErrorMessage), response)
        );
    }

    protected static void assertErrorMessages(List<String> expectedErrorMessages, GraphQLResponse response) {
        AtomicInteger errorIndex = new AtomicInteger(0);
        expectedErrorMessages.forEach(expectedErrorMessage -> {
            var error = response.errors().get(errorIndex.get());
            assertAll(
                    () -> assertNotNull(error.get(FIELD_MESSAGE)),
                    () -> assertEquals("\"" + expectedErrorMessage + "\"", error.get(FIELD_MESSAGE).toString())
            );
            errorIndex.incrementAndGet();
        });
    }

    protected static void assertErrormessageCodes(Set<String> expectedErrormessageCodes, GraphQLResponse response) {
        assertErrormessageCodes(expectedErrormessageCodes, 1, response);
    }

    protected static void assertErrormessageCodes(Set<String> expectedErrormessageCodes, int expectedErrorCount, GraphQLResponse response) {
        assertErrorCount(expectedErrorCount, response);

        AtomicInteger errorIndex = new AtomicInteger(0);
        while (errorIndex.get() < expectedErrorCount) {
            var error = response.errors().get(errorIndex.get());

            assertAll(
                    () -> assertNotNull(error.get(FIELD_EXTENSIONS)),
                    () -> assertNotNull(error.get(FIELD_EXTENSIONS).get(FIELD_ERRORMESSAGES))
            );

            var expectedSortedErrormessageCodes = expectedErrormessageCodes.stream().sorted(String::compareTo).toList();
            var actualErrormessageCodes = error.get(FIELD_EXTENSIONS).get(FIELD_ERRORMESSAGES).findValues(FIELD_CODE).stream()
                    .map(JsonNode::toString)
                    .toList();

            assertAll(
                    () -> assertErrormessagesCount(expectedErrormessageCodes.size(), error),
                    () -> assertIterableEquals(expectedSortedErrormessageCodes, actualErrormessageCodes)
            );

            errorIndex.incrementAndGet();
        }
    }

    protected static void assertErrormessageDetails(Set<String> expectedErrormessageDetails, GraphQLResponse response) {
        assertErrormessageDetails(expectedErrormessageDetails, 1, response);
    }

    protected static void assertErrormessageDetails(Set<String> expectedErrormessageDetails, int expectedErrorCount, GraphQLResponse response) {
        assertErrorCount(expectedErrorCount, response);

        AtomicInteger errorIndex = new AtomicInteger(0);
        while (errorIndex.get() < expectedErrorCount) {
            var error = response.errors().get(errorIndex.get());

            assertAll(
                    () -> assertNotNull(error.get(FIELD_EXTENSIONS)),
                    () -> assertNotNull(error.get(FIELD_EXTENSIONS).get(FIELD_ERRORMESSAGES))
            );

            var actualErrormessageDetails = error.get(FIELD_EXTENSIONS).get(FIELD_ERRORMESSAGES).findValues(FIELD_DETAILS).stream()
                    .map(JsonNode::toString)
                    .toList();

            assertAll(
                    () -> assertErrormessagesCount(expectedErrormessageDetails.size(), error),
                    () -> assertIterableEquals(expectedErrormessageDetails, actualErrormessageDetails)
            );

            errorIndex.incrementAndGet();
        }
    }

    private static void assertSingleError(GraphQLResponse response) {
        assertErrorCount(1, response);
    }

    private static void assertErrorCount(int expectedErrorCount, GraphQLResponse response) {
        assertAll(
                () -> assertNotNull(response.errors()),
                () -> assertEquals(expectedErrorCount, response.errors().size())
        );
    }

    private static void assertErrormessagesCount(int expectedCount, JsonNode error) {
        assertAll(
                () -> assertNotNull(error.get(FIELD_EXTENSIONS)),
                () -> assertEquals(expectedCount, error.get(FIELD_EXTENSIONS).get(FIELD_ERRORMESSAGES).findValues(FIELD_CODE).size())
        );
    }

    protected void assertAuditPermit(ArgumentCaptor<ILoggingEvent> loggingEventArgumentCaptor, String personId, int dataresponsAntall) {
        assertAudit(loggingEventArgumentCaptor, Level.INFO, personId, Optional.empty(), dataresponsAntall);
    }

    protected void assertAuditPermit(ArgumentCaptor<ILoggingEvent> loggingEventArgumentCaptor, String personId, Optional<String> korrelasjonId, int dataresponsAntall) {
        assertAudit(loggingEventArgumentCaptor, Level.INFO, personId, korrelasjonId, dataresponsAntall);
    }

    protected void assertAuditDeny(ArgumentCaptor<ILoggingEvent> loggingEventArgumentCaptor, String personId) {
        assertAudit(loggingEventArgumentCaptor, Level.WARN, personId, Optional.empty(), 0);
    }

    private void assertAudit(ArgumentCaptor<ILoggingEvent> loggingEventArgumentCaptor, Level level, String personId, Optional<String> korrelasjonId, int dataresponsAntall) {
        var loggingEventMessage = loggingEventArgumentCaptor.getAllValues().stream()
                .map(ILoggingEvent::getMessage)
                .filter(message -> message.contains(personId))
                .collect(toSingleton());

        assertTrue(loggingEventMessage.startsWith(level.equals(WARN) ? ARBEIDSFORHOLD_ACCESS_LOGGING_WARN_EVENT_START : ARBEIDSFORHOLD_ACCESS_LOGGING_INFO_EVENT_START));

        var logmessageExtensions = loggingEventMessage.substring(level.equals(WARN) ? ARBEIDSFORHOLD_ACCESS_LOGGING_WARN_EVENT_START.length() : ARBEIDSFORHOLD_ACCESS_LOGGING_INFO_EVENT_START.length()).trim();

        assertAll(
                () -> assertTrue(logmessageExtensions.contains("dproc=" + MASKINPORTEN_CONSUMER)),
                () -> assertTrue(logmessageExtensions.contains("suid=" + MASKINPORTEN_SUPPLIER)),
                () -> assertTrue(logmessageExtensions.contains("duid=" + personId)),
                () -> assertTrue(logmessageExtensions.contains("sproc=" + korrelasjonId.orElse(""))),
                () -> assertTrue(logmessageExtensions.contains("end=")),
                () -> assertTrue(logmessageExtensions.contains("cs3=query")),
                () -> assertTrue(logmessageExtensions.contains("cn1=" + dataresponsAntall))
        );
    }

    @BeforeEach
    void setUp() {
        mockedAuditLogAppender = getMockedAuditLogAppender();
        mockedSystemLogAppender = getMockedSystemLogAppender();
        addAuthorization = true;
        useCustomClaims = false;
        customToken = null;
        setExpectedHttpStatus(HttpStatus.OK);
        aaregTilgangskontrollStub.setAdressebeskyttedePersoner(emptyList());
    }

    public void disableAuthorization() {
        this.addAuthorization = false;
    }

    public void setCustomClaims(String claim, Object value) {
        this.useCustomClaims = true;
        this.customToken = maskinportenStub.generateTokenWithOverride(SCOPE, claim, value);
    }

    public void setCustomClaimsToExclude(String... claimsToExclude) {
        this.useCustomClaims = true;
        this.customToken = maskinportenStub.generateTokenWithout(SCOPE, claimsToExclude);
    }

    protected GraphQLResponse query(GraphQLRequestBuilder requestBuilder) {
        return query(requestBuilder, requestBuilder.variables());
    }

    protected GraphQLResponse query(GraphQLRequestBuilder requestBuilder, Map<String, Object> variables) {
        var requestBody = graphQLRequestFromFile(requestBuilder.requestFile(), variables);

        String auth = null;
        if (addAuthorization && !useCustomClaims) {
            auth = maskinportenStub.generateToken(SCOPE);
        }
        if (useCustomClaims) {
            auth = customToken;
        }

        String finalAuth = auth;
        var result = restTestClient.post()
                .uri("/graphql")
                .body(requestBody)
                .headers(httpHeaders -> {
                    httpHeaders.setContentType(APPLICATION_JSON);
                    if (finalAuth != null) {
                        httpHeaders.setBearerAuth(finalAuth);
                    }
                    if (requestBuilder.headers().containsKey(CORRELATION_ID)) {
                        httpHeaders.set(CORRELATION_ID, requestBuilder.headers().get(CORRELATION_ID));
                    }
                })
                .exchange()
                .returnResult(GraphQLResponse.class);

        assertEquals(expectedHttpStatus, result.getStatus());

        if (requestBuilder.headers().containsKey(CORRELATION_ID)) {
            assertTrue(result.getRequestHeaders().getValuesAsList(CORRELATION_ID).contains(requestBuilder.headers().get(CORRELATION_ID)));
        }

        return result.getResponseBody();
    }

    @SneakyThrows
    private GraphQLRequest graphQLRequestFromFile(String file, Map<String, Object> variables) {
        var classPathResource = new ClassPathResource(file);
        var body = new String(readAllBytes(Path.of(classPathResource.getURI())));
        return new GraphQLRequest(body, variables);
    }
}
