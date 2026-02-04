package no.nav.aareg.dist.api.audit;

import lombok.Builder;
import lombok.Value;

import java.util.Set;

/*
TODO:
    Dette objektet bryter med føringen om at personer med adressebeskyttelse bør logges som WARN.
    Siden vi kan utlevere adressebeskyttede personer til arbeidsgivere eller personer som selv har adressebeskyttelse (personer som slår opp på seg selv) -
    Må vi tilpasse koden til å håntere dette.
 */
@Value
@Builder
public class Auditelement {

    String ressurs;

    Set<String> arbeidstakeridentifikatorer;

    String konsumentId;
    String databehandlerId;
    String brukerId;

    String kilde;

    String korrelasjonId;

    String dataforespoersel;
    String datafilter;

    int dataresponsAntall;
}
