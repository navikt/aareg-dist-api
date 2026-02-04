package no.nav.aareg.dist.api.validering;

public class GamleArbeidsgivernummerValidator {

    /**
     * Sjekker om en gitt tekststreng er et gyldig arbeidsgivernummer på
     * gammelt format.
     *
     * <ul>
     *     <li>Nummeret har 9 siffer og starter på 300, 301 eller 302</li>
     *     <li>Nummeret har 11 siffer</li>
     * </ul>
     *
     * @param arbeidsgivernummer streng eller null-verdi
     */
    public static boolean erGyldigGammelArbeidsgivernummer(String arbeidsgivernummer) {
        if (arbeidsgivernummer == null) {
            return false;
        }
        return arbeidsgivernummer.matches("^(300|301|302)\\d{6}$") || arbeidsgivernummer.matches("^\\d{11}$");
    }
}
