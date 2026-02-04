package no.nav.aareg.dist.api.validering;

import java.util.Arrays;

import static java.lang.Integer.parseInt;

public final class OrganisasjonsnummerValidator {

    private static final String ORGNR_FORMAT = "^[2-3|8-9]\\d{8}$";
    private static final int[] ORGNR_VEKTTALL = {3, 2, 7, 6, 5, 4, 3, 2};

    /**
     * Returns true if the input is a number with 9 digits, starting with 2, 3, 8 or 9
     *
     * @param str input number to validate
     * @return true if valid
     */
    private static boolean isNumberWithNineDigits(String str) {
        return str != null && str.matches(ORGNR_FORMAT);
    }

    /**
     * Kontrollerer at et organisasjonsnummer er gyldig ved bruk av modulus 11-sjekk som
     * beskrevet i Brønnøysundsregisteret. Gitt et orgnummer med ni siffer, hvor det siste
     * er kontrollsifferet:
     * <ol>
     *     <li>Multiplisér siffer 1-8 med sine respektive {@link OrganisasjonsnummerValidator#ORGNR_VEKTTALL vekttall}, og summer disse</li>
     *     <li>Beregn modulo 11 av summen for å finne resttallet</li>
     *     <li>Sjekk om kontrollsiffer=(11-rest). Hvis rest==0 må kontrollsiffer være 0. Hvis rest==10 er orgnummeret ugyldig</li>
     * </ol>
     * (Se: <a href="https://www.brreg.no/om-oss/oppgavene-vare/alle-registrene-vare/om-enhetsregisteret/organisasjonsnummeret/">Brønnøysundsregisterets dokumentasjon</a> )
     *
     * @param organisasjonsnummer En streng av 9 siffer
     */
    public static boolean erGyldigOrganisasjonsnummer(String organisasjonsnummer) {
        if (!isNumberWithNineDigits(organisasjonsnummer)) {
            return false;
        }
        var kontrollsiffer = parseInt(organisasjonsnummer.substring(8, 9));
        var organisasjonsnummerUtenKontrollsiffer = organisasjonsnummer.substring(0, 8);
        var organisasjonssifre = Arrays.stream(organisasjonsnummerUtenKontrollsiffer.split("")).mapToInt(Integer::parseInt).toArray();

        int sum = 0;
        for (int i = 0; i < 8; i++) {
            sum += ORGNR_VEKTTALL[i] * organisasjonssifre[i];
        }
        int mod11 = sum % 11;
        // Kontrollsiffer er 0, ettersom divisjonen "går opp"
        if (mod11 == 0 && kontrollsiffer == 0) {
            return true;
        }
        // Kontrollsiffer ville blitt 10, som er ugyldig
        if (mod11 == 1) {
            return false;
        }
        return 11 - mod11 == kontrollsiffer;
    }
}
