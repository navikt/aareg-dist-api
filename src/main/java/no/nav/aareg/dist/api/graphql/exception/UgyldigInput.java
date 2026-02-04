package no.nav.aareg.dist.api.graphql.exception;

import graphql.ErrorClassification;

import java.util.List;
import java.util.Map;

import static graphql.ErrorType.ValidationError;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

public class UgyldigInput extends AaregGraphQLException {

    public static final String ERRORMESSAGE = "Ugyldig input";

    public UgyldigInput(List<AaregDistFeilkode> feilkoder) {
        super(ERRORMESSAGE, Map.of(FIELD_ERRORMESSAGES, toErrormessages(feilkoder)));
    }

    private static Object toErrormessages(List<AaregDistFeilkode> feilliste) {
        return feilliste.stream().map(feilkode ->
                        Errormessage.builder()
                                .code(feilkode.getKode())
                                .message(feilkode.getBeskrivelse())
                                .details(feilkode.getDetaljer())
                                .build())
                .sorted(comparing(Errormessage::getCode, nullsLast(naturalOrder())))
                .toList();
    }

    @Override
    public ErrorClassification getErrorClassification() {
        return ValidationError;
    }
}
