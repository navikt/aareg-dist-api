package no.nav.aareg.dist.api.validering;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.stream.IntStream;

import static java.lang.Integer.parseInt;

public class FolkeregisteridentValidator {

    private static final int[] vekter_k1 = { 3, 7, 6, 1, 8, 9, 4, 5, 2 };
    private static final int[] vekter_k2 = { 5, 4, 3, 2, 7, 6, 5, 4, 3, 2 };
    private static final int[] gyldigRest_k1 = { 0, 1, 2, 3 };
    private static final int gyldigRest_k2 = 0;
    private static final Integer[] dNummerSifre = { 4, 5, 6, 7 };

    /**
     * Validerer et fÃ¸dsels-eller-d-nummer(1964 og 2032-type) ved Ã¥ sjekke kontrollsifrene iht.
     * <a href="https://skatteetaten.github.io/folkeregisteret-api-dokumentasjon/nytt-fodselsnummer-fra-2032/">...</a>
     *
     * @param fnrdnr 11-siffret fÃ¸dsels-eller-D-nummer som skal valideres.
     * @return true hvis fÃ¸dsels-eller-D-nummer er gyldig, ellers false
     */
    public static boolean validerKontrollsifferFoedselsEllerDnummer(String fnrdnr) {
        final int[] sifre = konverterTilIntArray(fnrdnr);
        final int gitt_k1 = sifre[9];
        final int gitt_k2 = sifre[10];

        final int[] grunnlag_k1 = Arrays.copyOfRange(sifre, 0, vekter_k2.length);
        final int vektet_k1 = IntStream.range(0, vekter_k1.length)
                .map(i -> grunnlag_k1[i] * vekter_k1[i])
                .sum();

        final int beregnetRestSiffer_k1 = (vektet_k1 + gitt_k1) % 11;

        if (Arrays.stream(gyldigRest_k1).noneMatch(siffer -> siffer == beregnetRestSiffer_k1)) {
            return false;
        }

        final int[] grunnlag_k2 = Arrays.copyOfRange(sifre, 0, vekter_k2.length);
        final int vektet_k2 = IntStream.range(0, vekter_k2.length)
                .map(i -> grunnlag_k2[i] * vekter_k2[i])
                .sum();

        final int beregnetRestSiffer_k2 = (vektet_k2 + gitt_k2) % 11;

        return beregnetRestSiffer_k2 == gyldigRest_k2;
    }

    /**
     * Konverterer en streng til et array av heltall.
     *
     * @param streng strengen som skal konverteres
     * @return array av heltall
     */
    private static int[] konverterTilIntArray(String streng) {
        int[] ints = new int[streng.length()];

        for (int i = 0; i < streng.length(); i++) {
            ints[i] = parseInt(streng.substring(i, i + 1));
        }
        return ints;
    }

    /**
     * Validerer at gitt ID har gyldig format og dato fÃ¸r den kaller selve valideringen.
     *
     * @param gittNummer  ID-nummer som skal valideres.
     * @return true hvis ID-nummeret er gyldig, ellers kaster en IllegalArgumentException.
     * @throws IllegalArgumentException hvis ID-nummeret har ugyldig format eller ikke er gyldig bygget opp.
     */
    public static boolean erGyldigFolkergisterIdentifikator(String gittNummer) {
        if (gittNummer == null) {
            return false;
        }
        boolean gyldigFormat = gittNummer.matches("^\\d{11}$");

        if (!gyldigFormat) {
           return false;
        }

        String dato = gittNummer.substring(0, 6);

        if (erDnummer(gittNummer)) {
            int dagSiffer = Character.getNumericValue(dato.charAt(0));
            dato = (dagSiffer - 4) + dato.substring(1, 6);
        }

        int maanedSiffer = Character.getNumericValue(dato.charAt(2));

        if (maanedSiffer >= 8) { // Syntetisk Fokeregister FNR/DNR
            dato = dato.substring(0, 2) + (maanedSiffer - 8) + dato.substring(3, 6);
        } else if (maanedSiffer >= 6) { // Syntetisk Nav DNR
            dato = dato.substring(0, 2) + (maanedSiffer - 6) + dato.substring(3, 6);
        } else if (maanedSiffer >= 4) { // Syntetisk Nav FNR
            dato = dato.substring(0, 2) + (maanedSiffer - 4) + dato.substring(3, 6);
        }

        if (!erDatoGyldig(dato)) {
            return false;
        }

        if (!validerKontrollsifferFoedselsEllerDnummer(gittNummer)) {
            return false;
        }
        return true;
    }

    /**
     * Sjekker om et gitt nummer er et D-nummer.
     *
     * @param gittNummer Nummeret som skal sjekkes.
     * @return true hvis nummeret er et D-nummer, ellers false.
     */
    private static boolean erDnummer(String gittNummer) {
        return Arrays.asList(dNummerSifre).contains(Character.getNumericValue(gittNummer.charAt(0)));
    }

    /**
     * Sjekker om en gitt dato finnes pÃ¥ en kalender. Da Ã¥rhundre ikke lengre vil kunne utledes av
     * 2032-fÃ¸dselsnumre, antas alle datoer Ã¥ vÃ¦re etter Ã¥r 2000.
     *
     * @param dato Datoen som skal sjekkes i formatet ddMMyy.
     * @return true hvis datoen er gyldig, ellers false.
     */
    private static boolean erDatoGyldig(String dato) {
        final String aarhundre = "20";
        final String aar = dato.substring(4, 6);
        final String maaned = dato.substring(2, 4);
        final String dag = dato.substring(0, 2);
        final boolean erSkuddag = "2902".equals(dag + maaned);
        if (erSkuddag && !erSkuddaar(aar)) {
            return false;
        }
        try {
            LocalDate.parse(dag + maaned + aarhundre + aar, DateTimeFormatter.ofPattern("ddMMyyyy"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Utleder om et gitt Ã¥r er et skuddÃ¥r basert pÃ¥ kun to sifre. Dette medfÃ¸rer at man ikke
     * kan vite hvilket Ã¥rhundre det gjelder, sÃ¥ velger Ã¥ anse '00' som skuddÃ¥ret 2000.
     * Dette er grunnet i det ikke lengre vil vÃ¦re mulig Ã¥ utlede Ã¥rhundre av 2032-fÃ¸dselsnumre.
     *
     * @param aar Ã…ret som skal sjekkes i formatet 'yy'.
     * @return true hvis Ã¥ret er et skuddÃ¥r, ellers false.
     */
    private static boolean erSkuddaar(String aar) {
        return parseInt(aar) % 4 == 0;
    }
}
