package no.nav.aareg.dist.api.graphql;

import lombok.Data;
import lombok.experimental.Accessors;
import no.nav.aareg.dist.api.domain.FinnArbeidsforholdVariabler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static no.nav.aareg.dist.api.request.RequestHeaders.CORRELATION_ID;

@Data
@Accessors(fluent = true)
public class GraphQLRequestBuilder {

    public static final String FINN_ARBEIDSFORHOLD_VARIABLER = "finnArbeidsforholdVariabler";

    private Map<String, String> headers = new HashMap<>();

    private Map<String, Object> variables = new HashMap<>(Map.of(FINN_ARBEIDSFORHOLD_VARIABLER, new FinnArbeidsforholdVariabler()));

    private String requestFile;

    public static GraphQLRequestBuilder finnArbeidsforhold() {
        return new GraphQLRequestBuilder().requestFile("queries/finnArbeidsforhold.graphql");
    }

    public static GraphQLRequestBuilder finnArbeidsforhold(String requestFile, boolean inline) {
        return inline
                ? new GraphQLRequestBuilder().requestFile(requestFile).noVariables()
                : new GraphQLRequestBuilder().requestFile(requestFile);
    }

    public GraphQLRequestBuilder opplysningspliktigId(String opplysningspliktigId) {
        ((FinnArbeidsforholdVariabler) variables.get(FINN_ARBEIDSFORHOLD_VARIABLER)).setOpplysningspliktigId(opplysningspliktigId);
        return this;
    }

    public GraphQLRequestBuilder arbeidsstedId(String arbeidsstedId) {
        ((FinnArbeidsforholdVariabler) variables.get(FINN_ARBEIDSFORHOLD_VARIABLER)).setArbeidsstedId(arbeidsstedId);
        return this;
    }

    public GraphQLRequestBuilder arbeidstakerId(String arbeidstakerId) {
        ((FinnArbeidsforholdVariabler) variables.get(FINN_ARBEIDSFORHOLD_VARIABLER)).setArbeidstakerId(arbeidstakerId);
        return this;
    }

    public GraphQLRequestBuilder arbeidsforholdtype(Set<FinnArbeidsforholdVariabler.Arbeidsforholdtype> arbeidsforholdtype) {
        ((FinnArbeidsforholdVariabler) variables.get(FINN_ARBEIDSFORHOLD_VARIABLER)).setArbeidsforholdtype(arbeidsforholdtype);
        return this;
    }

    public GraphQLRequestBuilder rapporteringsordning(Set<FinnArbeidsforholdVariabler.Rapporteringsordning> rapporteringsordning) {
        ((FinnArbeidsforholdVariabler) variables.get(FINN_ARBEIDSFORHOLD_VARIABLER)).setRapporteringsordning(rapporteringsordning);
        return this;
    }

    public GraphQLRequestBuilder arbeidsforholdstatus(Set<FinnArbeidsforholdVariabler.Arbeidsforholdstatus> arbeidsforholdstatus) {
        ((FinnArbeidsforholdVariabler) variables.get(FINN_ARBEIDSFORHOLD_VARIABLER)).setArbeidsforholdstatus(arbeidsforholdstatus);
        return this;
    }

    public GraphQLRequestBuilder ansettelsesdetaljerhistorikk(boolean ansettelsesdetaljerhistorikk) {
        ((FinnArbeidsforholdVariabler) variables.get(FINN_ARBEIDSFORHOLD_VARIABLER)).setAnsettelsesdetaljerhistorikk(ansettelsesdetaljerhistorikk);
        return this;
    }

    public GraphQLRequestBuilder correlationId(String correlationId) {
        headers.put(CORRELATION_ID, correlationId);
        return this;
    }

    public GraphQLRequestBuilder withoutVariables() {
        variables.replace(FINN_ARBEIDSFORHOLD_VARIABLER, null);
        return this;
    }

    public GraphQLRequestBuilder noVariables() {
        variables = null;
        return this;
    }

    public GraphQLRequestBuilder withUgyldigVariabel() {
        var ugyldigVariabel = Map.of("", "");
        variables.replace(FINN_ARBEIDSFORHOLD_VARIABLER, ugyldigVariabel);
        return this;
    }
}
