package no.nav.aareg.dist.api.graphql.data;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.aareg.dist.api.domain.FinnArbeidsforholdVariabler;
import no.nav.aareg.dist.api.domain.api.v1.Arbeidsforholdliste;
import no.nav.aareg.dist.api.graphql.exception.TjenestefeilException;
import no.nav.aareg.dist.api.graphql.exception.UgyldigInput;
import no.nav.aareg.dist.api.graphql.validation.ValidationService;
import no.nav.aareg.dist.api.service.ArbeidsforholdDataService;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static graphql.execution.DataFetcherResult.newResult;
import static no.nav.aareg.dist.api.domain.FinnArbeidsforholdVariabler.FIELD_ANSETTELSESDETALJERHISTORIKK;
import static no.nav.aareg.dist.api.domain.FinnArbeidsforholdVariabler.FIELD_ARBEIDSFORHOLDSTATUS;
import static no.nav.aareg.dist.api.domain.FinnArbeidsforholdVariabler.FIELD_ARBEIDSFORHOLDTYPE;
import static no.nav.aareg.dist.api.domain.FinnArbeidsforholdVariabler.FIELD_ARBEIDSSTED_ID;
import static no.nav.aareg.dist.api.domain.FinnArbeidsforholdVariabler.FIELD_ARBEIDSTAKER_ID;
import static no.nav.aareg.dist.api.domain.FinnArbeidsforholdVariabler.FIELD_OPPLYSNINGSPLIKTIG_ID;
import static no.nav.aareg.dist.api.domain.FinnArbeidsforholdVariabler.FIELD_RAPPORTERINGSORDNING;
import static no.nav.aareg.dist.api.graphql.utils.GraphQLUtil.toGraphQLRequest;
import static no.nav.aareg.dist.api.metrics.MetricsConstants.GRAPHQL_QUERY_METRIC_NAME;
import static no.nav.aareg.dist.api.metrics.MetricsConstants.GRAPHQL_QUERY_METRIC_TAG;

@Slf4j
@Component
public class ArbeidsforholdDataFetcher implements DataFetcher<Object> {

    private static final Marker TEAM_LOGS_MARKER = MarkerFactory.getMarker("TEAM_LOGS");

    private final ArbeidsforholdDataService arbeidsforholdDataService;
    private final ValidationService validationService;

    public ArbeidsforholdDataFetcher(
            ArbeidsforholdDataService arbeidsforholdDataService,
            ValidationService validationService
    ) {
        this.arbeidsforholdDataService = arbeidsforholdDataService;
        this.validationService = validationService;
    }

    @Override
    @Timed(value = GRAPHQL_QUERY_METRIC_NAME, extraTags = {GRAPHQL_QUERY_METRIC_TAG, "Arbeidsforhold"}, percentiles = {0.5, 0.75, 0.99})
    public Object get(DataFetchingEnvironment dataFetchingEnvironment) {
        try {
            var lagredeArbeidsforhold = finnArbeidsforhold(dataFetchingEnvironment);
            return newResult().data(lagredeArbeidsforhold).build();
        } catch (UgyldigInput ui) {
            return ui.toDataFetcherResult();
        } catch (RestClientException e) {
            log.error(TEAM_LOGS_MARKER, "RestClientException kastet", e);
            return newResult().error(new TjenestefeilException()).build();
        } catch (Exception e) {
            log.error("En ukjent feil oppsto ved henting av arbeidsforhold", e);
            return newResult().error(new TjenestefeilException()).build();
        }
    }

    private Arbeidsforholdliste finnArbeidsforhold(DataFetchingEnvironment dataFetchingEnvironment) {
        var request = toGraphQLRequest(dataFetchingEnvironment, false);
        var finnArbeidsforholdVariabler = tilFinnArbeidsforholdVariabler(request.getVariables());
        validationService.validerInput(finnArbeidsforholdVariabler);
        var arbeidsforhold = arbeidsforholdDataService.hentArbeidsforhold(finnArbeidsforholdVariabler, dataFetchingEnvironment);

        //arbeidsforholdliste.sort(Arbeidsforhold.OPPRETTET_COMPARATOR);

        return Arbeidsforholdliste.builder()
                .arbeidsforhold(arbeidsforhold)
                .build();
    }

    private FinnArbeidsforholdVariabler tilFinnArbeidsforholdVariabler(Map<String, Object> variables) {
        var finnArbeidsforholdVariabler = (Map<String, Object>) variables.get("finnArbeidsforholdVariabler");
        if (finnArbeidsforholdVariabler == null) {
            return null;
        }
        var arbeidsforholdTyper = (ArrayList<String>) finnArbeidsforholdVariabler.getOrDefault(FIELD_ARBEIDSFORHOLDTYPE, new ArrayList<String>());
        var rapporteringsordninger = (ArrayList<String>) finnArbeidsforholdVariabler.getOrDefault(FIELD_RAPPORTERINGSORDNING, new ArrayList<String>());
        var arbeidsforholdStatuser = (ArrayList<String>) finnArbeidsforholdVariabler.getOrDefault(FIELD_ARBEIDSFORHOLDSTATUS, new ArrayList<String>());

        return FinnArbeidsforholdVariabler.builder()
                .opplysningspliktigId((String) finnArbeidsforholdVariabler.getOrDefault(FIELD_OPPLYSNINGSPLIKTIG_ID, null))
                .arbeidsstedId((String) finnArbeidsforholdVariabler.getOrDefault(FIELD_ARBEIDSSTED_ID, null))
                .arbeidstakerId((String) finnArbeidsforholdVariabler.getOrDefault(FIELD_ARBEIDSTAKER_ID, null))
                .arbeidsforholdtype(arbeidsforholdTyper != null ? arbeidsforholdTyper.stream().map(FinnArbeidsforholdVariabler.Arbeidsforholdtype::valueOf).collect(Collectors.toSet()) : Collections.emptySet())
                .rapporteringsordning(rapporteringsordninger != null ? rapporteringsordninger.stream().map(FinnArbeidsforholdVariabler.Rapporteringsordning::valueOf).collect(Collectors.toSet()) : Collections.emptySet())
                .arbeidsforholdstatus(arbeidsforholdStatuser != null ? arbeidsforholdStatuser.stream().map(FinnArbeidsforholdVariabler.Arbeidsforholdstatus::valueOf).collect(Collectors.toSet()) : Collections.emptySet())
                .ansettelsesdetaljerhistorikk((Boolean) finnArbeidsforholdVariabler.getOrDefault(FIELD_ANSETTELSESDETALJERHISTORIKK, Boolean.FALSE))
                .build();
    }
}
