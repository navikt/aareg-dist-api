package no.nav.aareg.dist.api.consumer.aareg.tilgangskontroll.dto;

public record Tilgang(
        String opplysningspliktigIdentifikator,
        String arbeidsstedIdentifikator,
        String arbeidstakerIdentifkator,
        String aarsakTilIngenTilgang,
        boolean harTilgang,
        boolean adressebeskyttetOpplysningspliktig,
        boolean adressebeskyttetArbeidstaker
) {
}
