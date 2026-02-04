package no.nav.aareg.dist.api.service;

import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import no.nav.aareg.dist.api.audit.AuditLogger;
import no.nav.aareg.dist.api.audit.Auditelement;
import no.nav.aareg.dist.api.config.security.MaskinportenClaimsService;
import no.nav.aareg.dist.api.consumer.aareg.mottak.AaregDistMottakConsumer;
import no.nav.aareg.dist.api.consumer.aareg.mottak.dto.HentArbeidsforholdRequestDTO;
import no.nav.aareg.dist.api.consumer.aareg.tilgangskontroll.dto.Tilgang;
import no.nav.aareg.dist.api.domain.AaregDistMottakApiV1ToAaregDistApiV1Mapper;
import no.nav.aareg.dist.api.domain.FinnArbeidsforholdVariabler;
import no.nav.aareg.dist.api.domain.api.v1.Arbeidsforhold;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static no.nav.aareg.dist.api.config.security.MaskinportenClaimsService.CONSUMER;
import static no.nav.aareg.dist.api.config.security.MaskinportenClaimsService.SUPPLIER;
import static no.nav.aareg.dist.api.domain.mottak.api.v1.Identtype.FOLKEREGISTERIDENT;
import static no.nav.aareg.dist.api.graphql.utils.GraphQLUtil.toCompactJson;
import static no.nav.aareg.dist.api.graphql.utils.GraphQLUtil.toGraphQLRequest;
import static no.nav.aareg.dist.api.request.RequestHeaders.CORRELATION_ID;
import static org.springframework.util.StringUtils.hasText;

@Service
@RequiredArgsConstructor
public class ArbeidsforholdDataService {


    private final AaregDistMottakConsumer aaregDistMottakConsumer;

    private final TilgangskontrollService tilgangskontrollService;

    private final MaskinportenClaimsService maskinportenClaimsService;

    private final AuditLogger auditLogger;

    @Value("${app.name}")
    private String appName;

    public List<Arbeidsforhold> hentArbeidsforhold(FinnArbeidsforholdVariabler finnArbeidsforholdVariabler, DataFetchingEnvironment dataFetchingEnvironment) {

        var hentArbeidsforholdRequest = getHentArbeidsforholdRequestDTO(finnArbeidsforholdVariabler);

        var arbeidsforholdliste = aaregDistMottakConsumer.hentArbeidsforhold(hentArbeidsforholdRequest).getArbeidsforhold();

        if (!Objects.equals(finnArbeidsforholdVariabler.getAnsettelsesdetaljerhistorikk(), TRUE)) {
            arbeidsforholdliste.forEach(arbeidsforhold ->
                    arbeidsforhold.getAnsettelsesdetaljer().removeIf(ansettelsesdetaljer -> ansettelsesdetaljer.getRapporteringsmaanedTil() != null)
            );
        }

        var tilganger = tilgangskontrollService.hentTilganger(arbeidsforholdliste, hentArbeidsforholdRequest);
        var filtrertArbeidsforholdliste = tilgangskontrollService.filtrerArbeidsforhold(arbeidsforholdliste, tilganger);

        if ((filtrertArbeidsforholdliste.size() < arbeidsforholdliste.size()) && hasText(finnArbeidsforholdVariabler.getArbeidstakerId())) {
            loggIngenTilgang(finnArbeidsforholdVariabler.getArbeidstakerId(), dataFetchingEnvironment);
        } else if ((filtrertArbeidsforholdliste.isEmpty() && hasText(finnArbeidsforholdVariabler.getArbeidstakerId()))) {
            loggTilgangUtenData(finnArbeidsforholdVariabler.getArbeidstakerId(), dataFetchingEnvironment, tilganger);
        } else {
            loggTilgang(filtrertArbeidsforholdliste, dataFetchingEnvironment, tilganger);
        }

        return filtrertArbeidsforholdliste.stream()
                .map(AaregDistMottakApiV1ToAaregDistApiV1Mapper::map)
                .toList();
    }

    private static HentArbeidsforholdRequestDTO getHentArbeidsforholdRequestDTO(FinnArbeidsforholdVariabler finnArbeidsforholdVariabler) {
        var arbeidsforholdtyper = finnArbeidsforholdVariabler.getArbeidsforholdtype()
                .stream()
                .map(type -> HentArbeidsforholdRequestDTO.Arbeidsforholdtype.valueOf(type.name()))
                .collect(Collectors.toSet());

        var rapporteringsordninger = finnArbeidsforholdVariabler.getRapporteringsordning()
                .stream()
                .map(rapporteringsordning -> HentArbeidsforholdRequestDTO.Rapporteringsordning.valueOf(rapporteringsordning.name()))
                .collect(Collectors.toSet());

        var arbeidsforholdstatuser = finnArbeidsforholdVariabler.getArbeidsforholdstatus()
                .stream()
                .map(status -> HentArbeidsforholdRequestDTO.Arbeidsforholdstatus.valueOf(status.name()))
                .collect(Collectors.toSet());

        return HentArbeidsforholdRequestDTO.builder()
                .opplysningspliktigId(finnArbeidsforholdVariabler.getOpplysningspliktigId())
                .arbeidsstedId(finnArbeidsforholdVariabler.getArbeidsstedId())
                .arbeidstakerId(finnArbeidsforholdVariabler.getArbeidstakerId())
                .arbeidsforholdtyper(arbeidsforholdtyper)
                .rapporteringsordninger(rapporteringsordninger)
                .arbeidsforholdstatuser(arbeidsforholdstatuser)
                .historikk(finnArbeidsforholdVariabler.getAnsettelsesdetaljerhistorikk())
                .build();
    }

    private void loggTilgang(
            List<no.nav.aareg.dist.api.domain.mottak.api.v1.Arbeidsforhold> arbeidsforholdliste,
            DataFetchingEnvironment dataFetchingEnvironment,
            List<Tilgang> tilganger
    ) {
        var arbeidstakeridentifikatorer = arbeidsforholdliste.stream()
                .map(arbeidsforhold -> arbeidsforhold.getArbeidstaker().getGjeldendeIdent(FOLKEREGISTERIDENT))
                .collect(toUnmodifiableSet());
        var auditelement = opprettAuditelement(arbeidstakeridentifikatorer, arbeidsforholdliste.size(), dataFetchingEnvironment);
        auditLogger.loggTilgang(auditelement, tilganger);
    }

    private void loggTilgangUtenData(
            String arbeidstakerId,
            DataFetchingEnvironment dataFetchingEnvironment,
            List<Tilgang> tilganger
    ) {
        var auditelement = opprettAuditelement(Set.of(arbeidstakerId), 0, dataFetchingEnvironment);
        auditLogger.loggTilgang(auditelement, tilganger);
    }

    private void loggIngenTilgang(
            String arbeidstakerId,
            DataFetchingEnvironment dataFetchingEnvironment
    ) {
        var auditelement = opprettAuditelement(Set.of(arbeidstakerId), 0, dataFetchingEnvironment);
        auditLogger.loggIngenTilgang(auditelement);
    }

    private Auditelement opprettAuditelement(
            Set<String> arbeidstakeridentifikatorer,
            int antallArbeidsforhold,
            DataFetchingEnvironment dataFetchingEnvironment
    ) {
        var konsumentId = maskinportenClaimsService.hentOrgnrFraToken().get(CONSUMER);
        var databehandlerId = maskinportenClaimsService.hentOrgnrFraToken().get(SUPPLIER);
        var graphqlRequest = toGraphQLRequest(dataFetchingEnvironment, true);

        return Auditelement.builder()
                .ressurs(Arbeidsforhold.class.getSimpleName())
                .arbeidstakeridentifikatorer(arbeidstakeridentifikatorer)
                .konsumentId(konsumentId)
                .databehandlerId(databehandlerId)
                .kilde(appName)
                .korrelasjonId(MDC.get(CORRELATION_ID))
                .dataforespoersel(graphqlRequest.getQuery())
                .datafilter(toCompactJson(graphqlRequest.getVariables()))
                .dataresponsAntall(antallArbeidsforhold)
                .build();
    }
}
