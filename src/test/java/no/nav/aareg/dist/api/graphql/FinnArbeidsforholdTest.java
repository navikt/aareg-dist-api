package no.nav.aareg.dist.api.graphql;

import graphql.execution.AbortExecutionException;
import graphql.execution.InputMapDefinesTooManyFieldsException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.DataFetchingEnvironment;
import no.nav.aareg.dist.api.config.security.exception.MaskinportenTokenException;
import no.nav.aareg.dist.api.domain.FinnArbeidsforholdVariabler;
import no.nav.aareg.dist.api.graphql.exception.UgyldigInput;
import no.nav.aareg.dist.api.service.ArbeidsforholdDataService;
import no.nav.aareg.dist.api.testdata.ArbeidsforholdDistMottakTestdataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static ch.qos.logback.classic.Level.ERROR;
import static ch.qos.logback.classic.Level.INFO;
import static graphql.ErrorType.ExecutionAborted;
import static graphql.ErrorType.ValidationError;
import static java.util.UUID.randomUUID;
import static no.nav.aareg.dist.api.config.security.MaskinportenClaimsService.CONSUMER;
import static no.nav.aareg.dist.api.domain.FinnArbeidsforholdVariabler.Arbeidsforholdstatus.AKTIV;
import static no.nav.aareg.dist.api.domain.FinnArbeidsforholdVariabler.Arbeidsforholdstatus.AVSLUTTET;
import static no.nav.aareg.dist.api.domain.FinnArbeidsforholdVariabler.Arbeidsforholdtype.maritimtArbeidsforhold;
import static no.nav.aareg.dist.api.domain.FinnArbeidsforholdVariabler.Arbeidsforholdtype.ordinaertArbeidsforhold;
import static no.nav.aareg.dist.api.domain.FinnArbeidsforholdVariabler.Rapporteringsordning.A_ORDNINGEN;
import static no.nav.aareg.dist.api.graphql.GraphQLRequestBuilder.FINN_ARBEIDSFORHOLD_VARIABLER;
import static no.nav.aareg.dist.api.graphql.GraphQLRequestBuilder.finnArbeidsforhold;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistApiFeilkode.MANGLENDE_IDENTIFIKATOR;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistApiFeilkode.MANGLENDE_VARIABEL_ARBEIDSFORHOLDSTATUS;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistApiFeilkode.MANGLENDE_VARIABEL_ARBEIDSFORHOLDTYPE;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistApiFeilkode.MANGLENDE_VARIABEL_RAPPORTERINGSORDNING;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistApiFeilkode.UGYLDIG_DATA;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistApiFeilkode.UGYLDIG_VARIABEL_ARBEIDSSTED_ID;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistApiFeilkode.UGYLDIG_VARIABEL_ARBEIDSTAKER_ID;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistApiFeilkode.UGYLDIG_VARIABEL_OPPLSYNINGSPLIKTIG_ID;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistApiFeilkode.UGYLDIG_VARIABEL_VERDI;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistErrorMessage.ERRORMESSAGE_INTERN_FEIL;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistErrorMessage.ERRORMESSAGE_UGYLDIG_DATA;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistFeilkode.INTERN_FEIL;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistFeilkode.UGYLDIG_SPOERRING;
import static no.nav.aareg.dist.api.testdata.ArbeidsforholdTestdataBuilder.ARBEIDSSTED_ID;
import static no.nav.aareg.dist.api.testdata.ArbeidsforholdTestdataBuilder.ARBEIDSTAKER_1_FNR;
import static no.nav.aareg.dist.api.testdata.ArbeidsforholdTestdataBuilder.ARBEIDSTAKER_2_FNR;
import static no.nav.aareg.dist.api.testdata.ArbeidsforholdTestdataBuilder.ARBEIDSTAKER_FNR;
import static no.nav.aareg.dist.api.testdata.ArbeidsforholdTestdataBuilder.ARBEIDSTAKER_FNR_MED_ADRESSESKJERMING;
import static no.nav.aareg.dist.api.testdata.ArbeidsforholdTestdataBuilder.OPPLYSNINGSPLIKTIG_ID;
import static no.nav.aareg.dist.api.util.LoggerMockingUtil.logMessageExists;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("GraphQL - Query - finnArbeidsforhold")
class FinnArbeidsforholdTest extends GraphQLTestBase {

    private static final Set<FinnArbeidsforholdVariabler.Arbeidsforholdtype> DEFAULT_ARBEIDSFORHOLDTYPE_SETT = Set.of(
            ordinaertArbeidsforhold,
            maritimtArbeidsforhold
    );

    private static final Set<FinnArbeidsforholdVariabler.Rapporteringsordning> DEFAULT_RAPPORTERINGSORDNING_SETT = Set.of(
            A_ORDNINGEN
    );

    private static final Set<FinnArbeidsforholdVariabler.Arbeidsforholdstatus> DEFAULT_ARBEIDSFORHOLDSTATUS_SETT = Set.of(
            AKTIV,
            AVSLUTTET
    );

    private final ArbeidsforholdDistMottakTestdataBuilder testdataBuilder = new ArbeidsforholdDistMottakTestdataBuilder();

    private String correlationId;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        correlationId = randomUUID().toString();
    }

    @Test
    @DisplayName("Skal finne tom arbeidsforholdliste")
    void scenario01() {
        aaregDistMottakStub.setArbeidsforholdResponse(List.of());

        var response = query(finnArbeidsforhold()
                .opplysningspliktigId(OPPLYSNINGSPLIKTIG_ID)
                .arbeidsstedId(ARBEIDSSTED_ID)
                .arbeidstakerId(ARBEIDSTAKER_FNR)
                .arbeidsforholdtype(DEFAULT_ARBEIDSFORHOLDTYPE_SETT)
                .rapporteringsordning(DEFAULT_RAPPORTERINGSORDNING_SETT)
                .arbeidsforholdstatus(DEFAULT_ARBEIDSFORHOLDSTATUS_SETT)
        );

        assertAll(
                () -> assertNoData(response),
                () -> assertNoErrors(response),
                () -> verify(mockedAuditLogAppender, times(1)).doAppend(auditLogEventArgumentCaptor.capture()),
                () -> assertAuditPermit(auditLogEventArgumentCaptor, ARBEIDSTAKER_FNR, 0)
        );
    }

    @Test
    @DisplayName("Skal finne arbeidsforholdliste for arbeidstaker med ordinært og maritimt arbeidsforhold")
    void scenario02() {
        var arbeidsforholdOrdinaert = testdataBuilder.defaultArbeidsforhold()
                .withArbeidsforholdType(ordinaertArbeidsforhold.name())
                .build();
        var arbeidsforholdMaritimt = testdataBuilder.defaultArbeidsforhold()
                .withArbeidsforholdType(maritimtArbeidsforhold.name())
                .build();

        var arbeidsforholdliste = List.of(arbeidsforholdOrdinaert, arbeidsforholdMaritimt);

        aaregDistMottakStub.setArbeidsforholdResponse(arbeidsforholdliste);

        var correlationId = "my-correlation-id";

        var response = query(finnArbeidsforhold()
                .correlationId(correlationId)
                .opplysningspliktigId(OPPLYSNINGSPLIKTIG_ID)
                .arbeidsstedId(ARBEIDSSTED_ID)
                .arbeidstakerId(ARBEIDSTAKER_FNR)
                .arbeidsforholdtype(DEFAULT_ARBEIDSFORHOLDTYPE_SETT)
                .rapporteringsordning(DEFAULT_RAPPORTERINGSORDNING_SETT)
                .arbeidsforholdstatus(DEFAULT_ARBEIDSFORHOLDSTATUS_SETT)
                .ansettelsesdetaljerhistorikk(true)
        );

        assertAll(
                () -> assertDataCount(arbeidsforholdliste.size(), response),
                () -> assertNoErrors(response),
                () -> verify(mockedAuditLogAppender, times(1)).doAppend(auditLogEventArgumentCaptor.capture()),
                () -> assertAuditPermit(auditLogEventArgumentCaptor, ARBEIDSTAKER_FNR, Optional.of(correlationId), arbeidsforholdliste.size())
        );
    }

    @Test
    @DisplayName("Skal bare returnere arbeidsforhold for arbeidstaker(e) som IKKE har adresseskjerming")
    void scenario03() {
        var arbeidsforholdUtenAdresseskjerming = testdataBuilder.defaultArbeidsforhold().withArbeidstaker(ARBEIDSTAKER_FNR).build();
        var arbeidsforholdMedAdresseskjerming = testdataBuilder.defaultArbeidsforhold().withArbeidstaker(ARBEIDSTAKER_FNR_MED_ADRESSESKJERMING).build();

        var arbeidsforholdliste = List.of(arbeidsforholdUtenAdresseskjerming, arbeidsforholdMedAdresseskjerming);

        aaregDistMottakStub.setArbeidsforholdResponse(arbeidsforholdliste);

        aaregTilgangskontrollStub.setAdressebeskyttedePersoner(List.of(ARBEIDSTAKER_FNR_MED_ADRESSESKJERMING));

        var response = query(finnArbeidsforhold()
                .opplysningspliktigId(OPPLYSNINGSPLIKTIG_ID)
                .arbeidsforholdtype(DEFAULT_ARBEIDSFORHOLDTYPE_SETT)
                .rapporteringsordning(DEFAULT_RAPPORTERINGSORDNING_SETT)
                .arbeidsforholdstatus(DEFAULT_ARBEIDSFORHOLDSTATUS_SETT)
                .ansettelsesdetaljerhistorikk(true)
        );

        assertDataExists(response);

        var responsArbeidsforholdliste = response.data().finnArbeidsforhold().getArbeidsforhold();

        assertAll(
                () -> assertNoErrors(response),
                () -> assertTrue(responsArbeidsforholdliste.size() < arbeidsforholdliste.size()),
                () -> assertTrue(responsArbeidsforholdliste.stream().map(arbeidsforhold -> arbeidsforhold.getArbeidstaker().getIdent()).toList().contains(ARBEIDSTAKER_FNR)),
                () -> assertFalse(responsArbeidsforholdliste.stream().map(arbeidsforhold -> arbeidsforhold.getArbeidstaker().getIdent()).toList().contains(ARBEIDSTAKER_FNR_MED_ADRESSESKJERMING)),
                () -> verify(mockedAuditLogAppender, times(1)).doAppend(auditLogEventArgumentCaptor.capture()),
                () -> assertAuditPermit(auditLogEventArgumentCaptor, ARBEIDSTAKER_FNR, responsArbeidsforholdliste.size())
        );
    }

    @Test
    @DisplayName("Skal ikke returnere arbeidsforhold når søk på arbeidstaker som har adresseskjerming")
    void scenario04() {
        var arbeidsforholdMedAdresseskjerming = testdataBuilder.defaultArbeidsforhold().withArbeidstaker(ARBEIDSTAKER_FNR_MED_ADRESSESKJERMING).build();

        var arbeidsforholdliste = List.of(arbeidsforholdMedAdresseskjerming);

        aaregDistMottakStub.setArbeidsforholdResponse(arbeidsforholdliste);

        aaregTilgangskontrollStub.setAdressebeskyttedePersoner(List.of(ARBEIDSTAKER_FNR_MED_ADRESSESKJERMING));

        var response = query(finnArbeidsforhold()
                .arbeidstakerId(ARBEIDSTAKER_FNR_MED_ADRESSESKJERMING)
                .arbeidsforholdtype(DEFAULT_ARBEIDSFORHOLDTYPE_SETT)
                .rapporteringsordning(DEFAULT_RAPPORTERINGSORDNING_SETT)
                .arbeidsforholdstatus(DEFAULT_ARBEIDSFORHOLDSTATUS_SETT)
                .ansettelsesdetaljerhistorikk(true)
        );

        assertAll(
                () -> assertNoErrors(response),
                () -> assertNoData(response),
                () -> verify(mockedAuditLogAppender, times(1)).doAppend(auditLogEventArgumentCaptor.capture()),
                () -> assertAuditDeny(auditLogEventArgumentCaptor, ARBEIDSTAKER_FNR_MED_ADRESSESKJERMING)
        );
    }

    @Test
    @DisplayName("Skal finne arbeidsforholdliste for arbeidssted")
    void scenario05() {
        var arbeidsforholdArbeidstaker1 = testdataBuilder.defaultArbeidsforhold()
                .withArbeidssted(ARBEIDSSTED_ID)
                .withArbeidstaker(ARBEIDSTAKER_1_FNR)
                .build();
        var arbeidsforholdArbeidstaker2 = testdataBuilder.defaultArbeidsforhold()
                .withArbeidssted(ARBEIDSSTED_ID)
                .withArbeidstaker(ARBEIDSTAKER_2_FNR)
                .build();

        var arbeidsforholdliste = List.of(arbeidsforholdArbeidstaker1, arbeidsforholdArbeidstaker2);

        aaregDistMottakStub.setArbeidsforholdResponse(arbeidsforholdliste);

        var response = query(finnArbeidsforhold()
                .arbeidsstedId(ARBEIDSSTED_ID)
                .arbeidsforholdtype(DEFAULT_ARBEIDSFORHOLDTYPE_SETT)
                .rapporteringsordning(DEFAULT_RAPPORTERINGSORDNING_SETT)
                .arbeidsforholdstatus(DEFAULT_ARBEIDSFORHOLDSTATUS_SETT)
                .ansettelsesdetaljerhistorikk(true)
        );

        assertDataExists(response);

        var responsArbeidsforholdliste = response.data().finnArbeidsforhold().getArbeidsforhold();

        assertAll(
                () -> assertDataCount(arbeidsforholdliste.size(), response),
                () -> assertNoErrors(response),
                () -> verify(mockedAuditLogAppender, times(2)).doAppend(auditLogEventArgumentCaptor.capture()),
                () -> assertAuditPermit(auditLogEventArgumentCaptor, ARBEIDSTAKER_1_FNR, responsArbeidsforholdliste.size()),
                () -> assertAuditPermit(auditLogEventArgumentCaptor, ARBEIDSTAKER_2_FNR, responsArbeidsforholdliste.size())
        );
    }

    @Test
    @DisplayName("Skal finne arbeidsforholdliste for 'inline-query'")
    void scenario06() {
        var arbeidsforhold = testdataBuilder.defaultArbeidsforhold().build();

        var arbeidsforholdliste = List.of(arbeidsforhold);

        aaregDistMottakStub.setArbeidsforholdResponse(arbeidsforholdliste);

        var response = query(finnArbeidsforhold("queries/finnArbeidsforholdInlineQuery.graphql", true));

        assertAll(
                () -> assertDataCount(arbeidsforholdliste.size(), response),
                () -> assertNoErrors(response)
        );
    }

    @Test
    @DisplayName("Feiler med inputvalideringsfeil: AA-001 [UgyldigInput]")
    void scenario10() {
        var response = query(finnArbeidsforhold().correlationId(correlationId).withoutVariables());

        var expectedErrormessageCodes = getExpectedErrormessageCodes(
                UGYLDIG_SPOERRING // AA-001
        );

        var expectedErrormessageDetails = getExpectedErrormessageDetails(
                UGYLDIG_SPOERRING // AA-001
        );

        assertAll(
                () -> assertErrorMessage(UgyldigInput.ERRORMESSAGE, response),
                () -> assertErrormessageCodes(expectedErrormessageCodes, response),
                () -> assertErrormessageDetails(expectedErrormessageDetails, response),
                () -> assertNoData(response),
                () -> verify(mockedSystemLogAppender, times(2)).doAppend(systemLogEventArgumentCaptor.capture()),
                () -> assertTrue(logMessageExists(systemLogEventArgumentCaptor, INFO, correlationId, ValidationError.name() + "|" + UgyldigInput.class.getSimpleName()))
        );
    }

    @Test
    @DisplayName("Feiler med inputvalideringsfeil: AA-001 [InputMapDefinesTooManyFieldsException]")
    void scenario11() {
        var response = query(finnArbeidsforhold().correlationId(correlationId).withUgyldigVariabel());

        var expectedErrormessageCodes = getExpectedErrormessageCodes(
                UGYLDIG_SPOERRING // AA-001
        );

        var expectedErrormessageDetails = getExpectedErrormessageDetails(
                UGYLDIG_SPOERRING // AA-001
        );

        assertAll(
                () -> assertErrorMessage(UgyldigInput.ERRORMESSAGE, response),
                () -> assertErrormessageCodes(expectedErrormessageCodes, response),
                () -> assertErrormessageDetails(expectedErrormessageDetails, response),
                () -> assertNoData(response),
                () -> verify(mockedSystemLogAppender, times(2)).doAppend(systemLogEventArgumentCaptor.capture()),
                () -> assertTrue(logMessageExists(systemLogEventArgumentCaptor, INFO, correlationId, ValidationError.name() + "|" + InputMapDefinesTooManyFieldsException.class.getSimpleName()))
        );
    }

    @Test
    @DisplayName("Feiler med inputvalideringsfeil: AA-001 [ValidationError]")
    void scenario12() {
        var response = query(finnArbeidsforhold("queries/finnArbeidsforholdInvalidInlineQuery.graphql", true).correlationId(correlationId));

        var expectedErrormessageCodes = getExpectedErrormessageCodes(
                UGYLDIG_SPOERRING // AA-001
        );

        var expectedErrormessageDetails = getExpectedErrormessageDetails(
                UGYLDIG_SPOERRING // AA-001
        );

        assertAll(
                () -> assertErrorMessage(UgyldigInput.ERRORMESSAGE, response),
                () -> assertErrormessageCodes(expectedErrormessageCodes, response),
                () -> assertErrormessageDetails(expectedErrormessageDetails, response),
                () -> assertNoData(response),
                () -> verify(mockedSystemLogAppender, times(2)).doAppend(systemLogEventArgumentCaptor.capture()),
                () -> assertTrue(logMessageExists(systemLogEventArgumentCaptor, INFO, correlationId, ValidationError.name() + "|" + graphql.validation.ValidationError.class.getSimpleName()))
        );
    }

    @Test
    @DisplayName("Feiler med inputvalideringsfeil: AA-200, AA-201, AA-202, AA-203 [UgyldigInput]")
    void scenario20() {
        var response = query(finnArbeidsforhold().correlationId(correlationId));

        var expectedErrormessageCodes = getExpectedErrormessageCodes(
                MANGLENDE_IDENTIFIKATOR, // AA-200
                MANGLENDE_VARIABEL_ARBEIDSFORHOLDTYPE, // AA-201
                MANGLENDE_VARIABEL_RAPPORTERINGSORDNING, // AA-202
                MANGLENDE_VARIABEL_ARBEIDSFORHOLDSTATUS // AA-203
        );

        var expectedErrormessageDetails = getExpectedErrormessageDetails(
                MANGLENDE_IDENTIFIKATOR, // AA-200
                MANGLENDE_VARIABEL_ARBEIDSFORHOLDTYPE, // AA-201
                MANGLENDE_VARIABEL_RAPPORTERINGSORDNING, // AA-202
                MANGLENDE_VARIABEL_ARBEIDSFORHOLDSTATUS // AA-203
        );

        assertAll(
                () -> assertErrorMessage(UgyldigInput.ERRORMESSAGE, response),
                () -> assertErrormessageCodes(expectedErrormessageCodes, response),
                () -> assertErrormessageDetails(expectedErrormessageDetails, response),
                () -> assertNoData(response),
                () -> verify(mockedSystemLogAppender, times(2)).doAppend(systemLogEventArgumentCaptor.capture()),
                () -> assertTrue(logMessageExists(systemLogEventArgumentCaptor, INFO, correlationId, ValidationError.name() + "|" + UgyldigInput.class.getSimpleName()))
        );
    }

    @Test
    @DisplayName("Feiler med inputvalideringsfeil: AA-204, AA-205, AA-206 [UgyldigInput]")
    void scenario21() {
        var response = query(finnArbeidsforhold()
                .correlationId(correlationId)
                .opplysningspliktigId("ugyldig-opplysningspliktig-id")
                .arbeidsstedId("ugyldig-arbeidssted-id")
                .arbeidstakerId("ugyldig-arbeidstaker-id")
                .arbeidsforholdtype(DEFAULT_ARBEIDSFORHOLDTYPE_SETT)
                .rapporteringsordning(DEFAULT_RAPPORTERINGSORDNING_SETT)
                .arbeidsforholdstatus(DEFAULT_ARBEIDSFORHOLDSTATUS_SETT)
        );

        var expectedErrormessageCodes = getExpectedErrormessageCodes(
                UGYLDIG_VARIABEL_OPPLSYNINGSPLIKTIG_ID, // AA-204
                UGYLDIG_VARIABEL_ARBEIDSSTED_ID, // AA-205
                UGYLDIG_VARIABEL_ARBEIDSTAKER_ID // AA-206
        );

        var expectedErrormessageDetails = getExpectedErrormessageDetails(
                UGYLDIG_VARIABEL_OPPLSYNINGSPLIKTIG_ID, // AA-204
                UGYLDIG_VARIABEL_ARBEIDSSTED_ID, // AA-205
                UGYLDIG_VARIABEL_ARBEIDSTAKER_ID // AA-206
        );

        assertAll(
                () -> assertErrorMessage(UgyldigInput.ERRORMESSAGE, response),
                () -> assertErrormessageCodes(expectedErrormessageCodes, response),
                () -> assertErrormessageDetails(expectedErrormessageDetails, response),
                () -> assertNoData(response),
                () -> verify(mockedSystemLogAppender, times(2)).doAppend(systemLogEventArgumentCaptor.capture()),
                () -> assertTrue(logMessageExists(systemLogEventArgumentCaptor, INFO, correlationId, ValidationError.name() + "|" + UgyldigInput.class.getSimpleName()))
        );
    }

    @Test
    @DisplayName("Feiler med inputvalideringsfeil: AA-207 (ugyldig arbeidsforholdtype) [CoercingParseValueException]")
    void scenario22() {
        Map<String, Object> variables = Map.of(
                FINN_ARBEIDSFORHOLD_VARIABLER, Map.of(
                        "arbeidstakerId", ARBEIDSTAKER_FNR,
                        "arbeidsforholdtype", Set.of("ugyldig-arbeidsforholdtype"),
                        "rapporteringsordning", DEFAULT_RAPPORTERINGSORDNING_SETT,
                        "arbeidsforholdstatus", DEFAULT_ARBEIDSFORHOLDSTATUS_SETT
                )
        );

        var response = query(finnArbeidsforhold().correlationId(correlationId), variables);

        var expectedErrormessageCodes = getExpectedErrormessageCodes(
                UGYLDIG_VARIABEL_VERDI // AA-207
        );

        var expectedErrormessageDetails = new LinkedHashSet<>(List.of(
                "\"arbeidsforholdtype\""
        ));

        assertAll(
                () -> assertErrorMessage(UgyldigInput.ERRORMESSAGE, response),
                () -> assertErrormessageCodes(expectedErrormessageCodes, response),
                () -> assertErrormessageDetails(expectedErrormessageDetails, response),
                () -> assertNoData(response),
                () -> verify(mockedSystemLogAppender, times(2)).doAppend(systemLogEventArgumentCaptor.capture()),
                () -> assertTrue(logMessageExists(systemLogEventArgumentCaptor, INFO, correlationId, ValidationError.name() + "|" + CoercingParseValueException.class.getSimpleName()))
        );
    }

    @Test
    @DisplayName("Feiler med inputvalideringsfeil: AA-207 (ugyldig rapporteringsordning) [CoercingParseValueException]")
    void scenario23() {
        Map<String, Object> variables = Map.of(
                FINN_ARBEIDSFORHOLD_VARIABLER, Map.of(
                        "arbeidstakerId", ARBEIDSTAKER_FNR,
                        "arbeidsforholdtype", DEFAULT_ARBEIDSFORHOLDTYPE_SETT,
                        "rapporteringsordning", Set.of("ugyldig-rapporteringsordning"),
                        "arbeidsforholdstatus", DEFAULT_ARBEIDSFORHOLDSTATUS_SETT
                )
        );

        var response = query(finnArbeidsforhold().correlationId(correlationId), variables);

        var expectedErrormessageCodes = getExpectedErrormessageCodes(
                UGYLDIG_VARIABEL_VERDI // AA-207
        );

        var expectedErrormessageDetails = new LinkedHashSet<>(List.of(
                "\"rapporteringsordning\""
        ));

        assertAll(
                () -> assertErrorMessage(UgyldigInput.ERRORMESSAGE, response),
                () -> assertErrormessageCodes(expectedErrormessageCodes, response),
                () -> assertErrormessageDetails(expectedErrormessageDetails, response),
                () -> assertNoData(response),
                () -> verify(mockedSystemLogAppender, times(2)).doAppend(systemLogEventArgumentCaptor.capture()),
                () -> assertTrue(logMessageExists(systemLogEventArgumentCaptor, INFO, correlationId, ValidationError.name() + "|" + CoercingParseValueException.class.getSimpleName()))
        );
    }

    @Test
    @DisplayName("Feiler med inputvalideringsfeil: AA-207 (ugyldig arbeidsforholdstatus) [CoercingParseValueException]")
    void scenario24() {
        Map<String, Object> variables = Map.of(
                FINN_ARBEIDSFORHOLD_VARIABLER, Map.of(
                        "arbeidstakerId", ARBEIDSTAKER_FNR,
                        "arbeidsforholdtype", DEFAULT_ARBEIDSFORHOLDTYPE_SETT,
                        "rapporteringsordning", DEFAULT_RAPPORTERINGSORDNING_SETT,
                        "arbeidsforholdstatus", Set.of("ugyldig-arbeidsforholdstatus")
                )
        );

        var response = query(finnArbeidsforhold().correlationId(correlationId), variables);

        var expectedErrormessageCodes = getExpectedErrormessageCodes(
                UGYLDIG_VARIABEL_VERDI // AA-207
        );

        var expectedErrormessageDetails = new LinkedHashSet<>(List.of(
                "\"arbeidsforholdstatus\""
        ));

        assertAll(
                () -> assertErrorMessage(UgyldigInput.ERRORMESSAGE, response),
                () -> assertErrormessageCodes(expectedErrormessageCodes, response),
                () -> assertErrormessageDetails(expectedErrormessageDetails, response),
                () -> assertNoData(response),
                () -> verify(mockedSystemLogAppender, times(2)).doAppend(systemLogEventArgumentCaptor.capture()),
                () -> assertTrue(logMessageExists(systemLogEventArgumentCaptor, INFO, correlationId, ValidationError.name() + "|" + CoercingParseValueException.class.getSimpleName()))
        );
    }

    @Test
    @DisplayName("Feiler med 2 ugyldig data feil: AA-002 [NonNullableFieldWasNullError]")
    void scenario25() {
        var arbeidsforholdUtenOpprettet = testdataBuilder.defaultArbeidsforhold()
                .withArbeidsforholdType(ordinaertArbeidsforhold.name())
                .build();
        arbeidsforholdUtenOpprettet.setOpprettet(null);

        var arbeidsforholdUtenSistBekreftet = testdataBuilder.defaultArbeidsforhold()
                .withArbeidsforholdType(maritimtArbeidsforhold.name())
                .build();
        arbeidsforholdUtenOpprettet.setSistBekreftet(null);

        var arbeidsforholdliste = List.of(arbeidsforholdUtenOpprettet, arbeidsforholdUtenSistBekreftet);

        aaregDistMottakStub.setArbeidsforholdResponse(arbeidsforholdliste);

        var response = query(finnArbeidsforhold()
                .correlationId(correlationId)
                .arbeidstakerId(ARBEIDSTAKER_FNR)
                .arbeidsforholdtype(DEFAULT_ARBEIDSFORHOLDTYPE_SETT)
                .rapporteringsordning(DEFAULT_RAPPORTERINGSORDNING_SETT)
                .arbeidsforholdstatus(DEFAULT_ARBEIDSFORHOLDSTATUS_SETT)
                .ansettelsesdetaljerhistorikk(true)
        );

        var expectedErrormessageCodes = getExpectedErrormessageCodes(
                UGYLDIG_DATA, // AA-002
                UGYLDIG_DATA // AA-002
        );

        var expectedErrormessageDetails = getExpectedErrormessageDetails(
                UGYLDIG_DATA, // AA-002
                UGYLDIG_DATA // AA-002
        );

        var errorMessages = List.of(ERRORMESSAGE_UGYLDIG_DATA, ERRORMESSAGE_UGYLDIG_DATA);

        assertAll(
                () -> assertErrorMessages(errorMessages, response),
                () -> assertErrormessageCodes(expectedErrormessageCodes, errorMessages.size(), response),
                () -> assertErrormessageDetails(expectedErrormessageDetails, errorMessages.size(), response),
                () -> assertNoData(response),
                () -> verify(mockedAuditLogAppender, times(1)).doAppend(auditLogEventArgumentCaptor.capture()),
                () -> assertAuditPermit(auditLogEventArgumentCaptor, ARBEIDSTAKER_FNR, arbeidsforholdliste.size()),
                () -> verify(mockedSystemLogAppender, times(6)).doAppend(systemLogEventArgumentCaptor.capture())
        );
    }

    @Test
    @DisplayName("Feiler med intern feil når token ikke inneholder 'consumer': AA-000 [MaskinportenTokenException]")
    void scenario30() {
        setCustomClaimsToExclude(CONSUMER);

        var response = query(finnArbeidsforhold()
                .correlationId(correlationId)
                .opplysningspliktigId(OPPLYSNINGSPLIKTIG_ID)
                .arbeidsforholdtype(DEFAULT_ARBEIDSFORHOLDTYPE_SETT)
                .rapporteringsordning(DEFAULT_RAPPORTERINGSORDNING_SETT)
                .arbeidsforholdstatus(DEFAULT_ARBEIDSFORHOLDSTATUS_SETT)
        );

        var expectedErrormessageCodes = getExpectedErrormessageCodes(
                INTERN_FEIL // AA-000
        );

        var expectedErrormessageDetails = getExpectedErrormessageDetails(
                INTERN_FEIL // AA-000
        );

        assertAll(
                () -> assertErrorMessage(ERRORMESSAGE_INTERN_FEIL, response),
                () -> assertErrormessageCodes(expectedErrormessageCodes, response),
                () -> assertErrormessageDetails(expectedErrormessageDetails, response),
                () -> assertNoData(response),
                () -> verify(mockedAuditLogAppender, times(0)).doAppend(any()),
                () -> verify(mockedSystemLogAppender, times(4)).doAppend(systemLogEventArgumentCaptor.capture()),
                () -> assertTrue(logMessageExists(systemLogEventArgumentCaptor, ERROR, correlationId, ExecutionAborted.name())),
                () -> assertTrue(logMessageExists(systemLogEventArgumentCaptor, ERROR, correlationId, MaskinportenTokenException.class.getSimpleName()))
        );
    }

    @Test
    @DisplayName("Feiler med intern feil ved 'runtime-exception': AA-000 [RuntimeException]")
    void scenario31() {
        aaregDistMottakStub.setupForFailOnPost();

        var response = query(finnArbeidsforhold()
                .correlationId(correlationId)
                .opplysningspliktigId(OPPLYSNINGSPLIKTIG_ID)
                .arbeidsforholdtype(DEFAULT_ARBEIDSFORHOLDTYPE_SETT)
                .rapporteringsordning(DEFAULT_RAPPORTERINGSORDNING_SETT)
                .arbeidsforholdstatus(DEFAULT_ARBEIDSFORHOLDSTATUS_SETT)
        );

        var expectedErrormessageCodes = getExpectedErrormessageCodes(
                INTERN_FEIL // AA-000
        );

        var expectedErrormessageDetails = getExpectedErrormessageDetails(
                INTERN_FEIL // AA-000
        );

        assertAll(
                () -> assertErrorMessage(ERRORMESSAGE_INTERN_FEIL, response),
                () -> assertErrormessageCodes(expectedErrormessageCodes, response),
                () -> assertErrormessageDetails(expectedErrormessageDetails, response),
                () -> assertNoData(response),
                () -> verify(mockedAuditLogAppender, times(0)).doAppend(any()),
                () -> verify(mockedSystemLogAppender, times(4)).doAppend(systemLogEventArgumentCaptor.capture()),
                () -> assertTrue(logMessageExists(systemLogEventArgumentCaptor, ERROR, correlationId, ExecutionAborted.name()))
        );
    }

    @Test
    @DisplayName("Feiler med intern feil når en ikke-håndtert GraphQLException inntreffer: AA-000 [AbortExecutionException]")
    @Disabled
        // Alle feil skal være håndtert så denne situasjonen er vanskelig å tvinge fram
    void scenario33() {
        var arbeidsforholdDataServiceMock = mock(ArbeidsforholdDataService.class);

        when(arbeidsforholdDataServiceMock.hentArbeidsforhold(any(FinnArbeidsforholdVariabler.class), any(DataFetchingEnvironment.class))).thenThrow(new AbortExecutionException());

        var response = query(finnArbeidsforhold()
                .correlationId(correlationId)
                .arbeidstakerId(ARBEIDSTAKER_FNR)
                .arbeidsforholdtype(DEFAULT_ARBEIDSFORHOLDTYPE_SETT)
                .rapporteringsordning(DEFAULT_RAPPORTERINGSORDNING_SETT)
                .arbeidsforholdstatus(DEFAULT_ARBEIDSFORHOLDSTATUS_SETT)
                .ansettelsesdetaljerhistorikk(true)
        );

        var expectedErrormessageCodes = getExpectedErrormessageCodes(
                INTERN_FEIL // AA-000
        );

        var expectedErrormessageDetails = getExpectedErrormessageDetails(
                INTERN_FEIL // AA-000
        );

        assertAll(
                () -> assertErrorMessage(ERRORMESSAGE_INTERN_FEIL, response),
                () -> assertErrormessageCodes(expectedErrormessageCodes, response),
                () -> assertErrormessageDetails(expectedErrormessageDetails, response),
                () -> assertNoData(response),
                () -> verify(mockedAuditLogAppender, times(0)).doAppend(any()),
                () -> verify(mockedSystemLogAppender, times(2)).doAppend(systemLogEventArgumentCaptor.capture()),
                () -> assertTrue(logMessageExists(systemLogEventArgumentCaptor, ERROR, correlationId, ExecutionAborted.name())),
                () -> assertTrue(logMessageExists(systemLogEventArgumentCaptor, ERROR, correlationId, AbortExecutionException.class.getSimpleName()))
        );
    }

    @Test
    @DisplayName("Returnerer 401 og 'tom' respons når token ikke er satt i request til graphql")
    void scenario34() {
        disableAuthorization();
        setExpectedHttpStatus(HttpStatus.UNAUTHORIZED);

        var response = query(finnArbeidsforhold());

        assertNull(response);
    }

    @Test
    @DisplayName("Returnerer 401 og 'tom' respons når token har feil scope")
    void scenario35() {
        setCustomClaims("scope", "nav:noeannet/enn/v1/arbeidsforhold");
        setExpectedHttpStatus(HttpStatus.UNAUTHORIZED);

        var response = query(finnArbeidsforhold());

        assertNull(response);
    }
}