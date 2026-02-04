package no.nav.aareg.dist.api.validering;

import static no.nav.aareg.dist.api.validering.FolkeregisteridentValidator.erGyldigFolkergisterIdentifikator;
import static no.nav.aareg.dist.api.validering.GamleArbeidsgivernummerValidator.erGyldigGammelArbeidsgivernummer;
import static no.nav.aareg.dist.api.validering.OrganisasjonsnummerValidator.erGyldigOrganisasjonsnummer;

public final class IdentValidator {

    private IdentValidator() {
    }

    public static boolean erGyldigOpplysningpliktigId(String opplysningspliktigId) {
        return erGyldigFolkergisterIdentifikator(opplysningspliktigId) || erGyldigOrganisasjonsnummer(opplysningspliktigId) || erGyldigGammelArbeidsgivernummer(opplysningspliktigId);
    }

    public static boolean erGyldigArbeidsstedId(String arbeidsstedId) {
        return erGyldigFolkergisterIdentifikator(arbeidsstedId) || erGyldigOrganisasjonsnummer(arbeidsstedId) || erGyldigGammelArbeidsgivernummer(arbeidsstedId);
    }
}
