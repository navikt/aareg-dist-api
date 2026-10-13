package no.nav.aareg.dist.api.graphql.validation;

import no.nav.aareg.dist.api.domain.FinnArbeidsforholdVariabler;
import no.nav.aareg.dist.api.graphql.exception.AaregDistFeilkode;
import no.nav.aareg.dist.api.graphql.exception.UgyldigInput;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static no.nav.aareg.dist.api.graphql.exception.AaregDistApiFeilkode.MANGLENDE_IDENTIFIKATOR;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistApiFeilkode.MANGLENDE_VARIABEL_ARBEIDSFORHOLDSTATUS;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistApiFeilkode.MANGLENDE_VARIABEL_ARBEIDSFORHOLDTYPE;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistApiFeilkode.MANGLENDE_VARIABEL_RAPPORTERINGSORDNING;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistApiFeilkode.UGYLDIG_SPOERRING;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistApiFeilkode.UGYLDIG_VARIABEL_ANSATT_DATO_INTERVALL;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistApiFeilkode.UGYLDIG_VARIABEL_ANSATT_FRA;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistApiFeilkode.UGYLDIG_VARIABEL_ANSATT_TIL;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistApiFeilkode.UGYLDIG_VARIABEL_ARBEIDSSTED_ID;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistApiFeilkode.UGYLDIG_VARIABEL_ARBEIDSTAKER_ID;
import static no.nav.aareg.dist.api.graphql.exception.AaregDistApiFeilkode.UGYLDIG_VARIABEL_OPPLSYNINGSPLIKTIG_ID;
import static no.nav.aareg.dist.api.validering.FolkeregisteridentValidator.erGyldigFolkergisterIdentifikator;
import static no.nav.aareg.dist.api.validering.IdentValidator.erGyldigArbeidsstedId;
import static no.nav.aareg.dist.api.validering.IdentValidator.erGyldigOpplysningpliktigId;
import static org.springframework.util.StringUtils.hasText;

@Service
public class ValidationService {

    public void validerInput(FinnArbeidsforholdVariabler finnArbeidsforholdVariabler) {
        var valideringsfeil = new ArrayList<AaregDistFeilkode>();

        if (finnArbeidsforholdVariabler == null) {
            throw new UgyldigInput(List.of(UGYLDIG_SPOERRING));
        } else {
            validerIdentifikatorInput(finnArbeidsforholdVariabler, valideringsfeil);
            validerFilterInput(finnArbeidsforholdVariabler, valideringsfeil);
            validerAnsettelsesdatoInput(finnArbeidsforholdVariabler, valideringsfeil);
        }

        if (!valideringsfeil.isEmpty()) {
            throw new UgyldigInput(valideringsfeil);
        }
    }

    private void validerIdentifikatorInput(FinnArbeidsforholdVariabler finnArbeidsforholdVariabler, List<AaregDistFeilkode> valideringsfeil) {
        if (!hasText(finnArbeidsforholdVariabler.getOpplysningspliktigId())
                && !hasText(finnArbeidsforholdVariabler.getArbeidsstedId())
                && !hasText(finnArbeidsforholdVariabler.getArbeidstakerId())
        ) {
            valideringsfeil.add(MANGLENDE_IDENTIFIKATOR);
            return;
        }

        if (hasText(finnArbeidsforholdVariabler.getOpplysningspliktigId()) && !erGyldigOpplysningpliktigId(finnArbeidsforholdVariabler.getOpplysningspliktigId())) {
            valideringsfeil.add(UGYLDIG_VARIABEL_OPPLSYNINGSPLIKTIG_ID);
        }

        if (hasText(finnArbeidsforholdVariabler.getArbeidsstedId()) && !erGyldigArbeidsstedId(finnArbeidsforholdVariabler.getArbeidsstedId())) {
            valideringsfeil.add(UGYLDIG_VARIABEL_ARBEIDSSTED_ID);
        }

        if (hasText(finnArbeidsforholdVariabler.getArbeidstakerId()) && !erGyldigFolkergisterIdentifikator(finnArbeidsforholdVariabler.getArbeidstakerId())) {
            valideringsfeil.add(UGYLDIG_VARIABEL_ARBEIDSTAKER_ID);
        }
    }

    private void validerFilterInput(FinnArbeidsforholdVariabler finnArbeidsforholdVariabler, List<AaregDistFeilkode> valideringsfeil) {
        if (CollectionUtils.isEmpty(finnArbeidsforholdVariabler.getArbeidsforholdtype())) {
            valideringsfeil.add(MANGLENDE_VARIABEL_ARBEIDSFORHOLDTYPE);
        }
        if (CollectionUtils.isEmpty(finnArbeidsforholdVariabler.getRapporteringsordning())) {
            valideringsfeil.add(MANGLENDE_VARIABEL_RAPPORTERINGSORDNING);
        }
        if (CollectionUtils.isEmpty(finnArbeidsforholdVariabler.getArbeidsforholdstatus())) {
            valideringsfeil.add(MANGLENDE_VARIABEL_ARBEIDSFORHOLDSTATUS);
        }
    }

    private void validerAnsettelsesdatoInput(FinnArbeidsforholdVariabler finnArbeidsforholdVariabler, List<AaregDistFeilkode> valideringsfeil) {
        var ansattFra = parseDato(finnArbeidsforholdVariabler.getAnsattFra(), UGYLDIG_VARIABEL_ANSATT_FRA, valideringsfeil);
        var ansattTil = parseDato(finnArbeidsforholdVariabler.getAnsattTil(), UGYLDIG_VARIABEL_ANSATT_TIL, valideringsfeil);

        if (ansattFra != null && ansattTil != null && ansattFra.isAfter(ansattTil)) {
            valideringsfeil.add(UGYLDIG_VARIABEL_ANSATT_DATO_INTERVALL);
        }
    }

    private LocalDate parseDato(String dato, AaregDistFeilkode feilkode, List<AaregDistFeilkode> valideringsfeil) {
        if (dato == null) {
            return null;
        }
        try {
            return LocalDate.parse(dato);
        } catch (DateTimeParseException e) {
            valideringsfeil.add(feilkode);
            return null;
        }
    }
}
