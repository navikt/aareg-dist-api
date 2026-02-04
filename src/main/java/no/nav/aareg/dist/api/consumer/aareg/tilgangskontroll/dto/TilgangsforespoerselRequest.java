package no.nav.aareg.dist.api.consumer.aareg.tilgangskontroll.dto;

import java.util.Set;

public record TilgangsforespoerselRequest(
        Kontekst kontekst,
        Operasjon operasjon,
        Set<Arbeidstaker> forespurteArbeidstakere
) {

    public record Arbeidstaker(
            String opplysningspliktigIdentifikator,
            String arbeidsstedIdentifikator,
            String arbeidstakerIdentifikator
    ) {
    }

    public enum Operasjon {
        LESE,
        SKRIVE,
    }
}
