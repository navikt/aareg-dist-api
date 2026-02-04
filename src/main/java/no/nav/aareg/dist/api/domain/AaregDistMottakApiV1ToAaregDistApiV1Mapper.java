package no.nav.aareg.dist.api.domain;

import no.nav.aareg.dist.api.domain.api.v1.Ansettelsesdetaljer;
import no.nav.aareg.dist.api.domain.api.v1.Ansettelsesperiode;
import no.nav.aareg.dist.api.domain.api.v1.Arbeidsforhold;
import no.nav.aareg.dist.api.domain.api.v1.Arbeidssted;
import no.nav.aareg.dist.api.domain.api.v1.Arbeidstaker;
import no.nav.aareg.dist.api.domain.api.v1.IdHistorikk;
import no.nav.aareg.dist.api.domain.api.v1.Kodeverksentitet;
import no.nav.aareg.dist.api.domain.api.v1.Opplysningspliktig;
import no.nav.aareg.dist.api.domain.api.v1.Permisjon;
import no.nav.aareg.dist.api.domain.api.v1.PermisjonPermittering;
import no.nav.aareg.dist.api.domain.api.v1.Permittering;
import no.nav.aareg.dist.api.domain.api.v1.Rapporteringsmaaneder;
import no.nav.aareg.dist.api.domain.api.v1.TimerMedTimeloenn;
import no.nav.aareg.dist.api.domain.api.v1.Utenlandsopphold;
import no.nav.aareg.dist.api.domain.api.v1.Varsel;
import no.nav.aareg.dist.api.domain.api.v1.Varselentitet;
import no.nav.aareg.dist.api.domain.mottak.api.v1.ForenkletAnsettelsesdetaljer;
import no.nav.aareg.dist.api.domain.mottak.api.v1.FrilanserAnsettelsesdetaljer;
import no.nav.aareg.dist.api.domain.mottak.api.v1.Hovedenhet;
import no.nav.aareg.dist.api.domain.mottak.api.v1.MaritimAnsettelsesdetaljer;
import no.nav.aareg.dist.api.domain.mottak.api.v1.OrdinaerAnsettelsesdetaljer;
import no.nav.aareg.dist.api.domain.mottak.api.v1.Person;
import no.nav.aareg.dist.api.domain.mottak.api.v1.Underenhet;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;

public class AaregDistMottakApiV1ToAaregDistApiV1Mapper {

    private static String finnGjeldendeFolkeregisterIdent(List<no.nav.aareg.dist.api.domain.mottak.api.v1.Ident> identer) {
        if (identer == null) {
            return null;
        }
        return identer.stream()
                .filter(ident -> no.nav.aareg.dist.api.domain.mottak.api.v1.Identtype.FOLKEREGISTERIDENT.equals(ident.getType()) && TRUE.equals(ident.getGjeldende()))
                .map(no.nav.aareg.dist.api.domain.mottak.api.v1.Ident::getIdent)
                .findFirst()
                .orElse(null);
    }

    public static Arbeidsforhold map(no.nav.aareg.dist.api.domain.mottak.api.v1.Arbeidsforhold arbeidsforhold) {
        if (arbeidsforhold == null) {
            return null;
        }
        return new Arbeidsforhold(
                arbeidsforhold.getId(),
                map(arbeidsforhold.getType()),
                map(arbeidsforhold.getArbeidstaker()),
                map(arbeidsforhold.getArbeidssted()),
                map(arbeidsforhold.getOpplysningspliktig()),
                map(arbeidsforhold.getAnsettelsesperiode()),
                mapList(AaregDistMottakApiV1ToAaregDistApiV1Mapper::map, arbeidsforhold.getAnsettelsesdetaljer()),
                mapList(AaregDistMottakApiV1ToAaregDistApiV1Mapper::map, arbeidsforhold.getPermisjoner()),
                mapList(AaregDistMottakApiV1ToAaregDistApiV1Mapper::map, arbeidsforhold.getPermitteringer()),
                mapList(AaregDistMottakApiV1ToAaregDistApiV1Mapper::map, arbeidsforhold.getTimerMedTimeloenn()),
                mapList(AaregDistMottakApiV1ToAaregDistApiV1Mapper::map, arbeidsforhold.getUtenlandsopphold()),
                mapList(AaregDistMottakApiV1ToAaregDistApiV1Mapper::map, arbeidsforhold.getIdHistorikk()),
                mapList(AaregDistMottakApiV1ToAaregDistApiV1Mapper::map, arbeidsforhold.getVarsler()),
                map(arbeidsforhold.getRapporteringsordning()),
                arbeidsforhold.getNavUuid(),
                arbeidsforhold.getOpprettet(),
                arbeidsforhold.getSistBekreftet(),
                arbeidsforhold.getSistEndret()
        );
    }

    private static Arbeidssted map(no.nav.aareg.dist.api.domain.mottak.api.v1.Arbeidssted arbeidssted) {
        if (arbeidssted == null) {
            return null;
        }
        if (arbeidssted instanceof Underenhet underenhet) {
            var distUnderenhet = new no.nav.aareg.dist.api.domain.api.v1.Underenhet();
            distUnderenhet.setIdent(underenhet.getIdent());
            return distUnderenhet;
        } else if (arbeidssted instanceof Person person) {
            var distPerson = new no.nav.aareg.dist.api.domain.api.v1.Person();
            distPerson.setIdent(finnGjeldendeFolkeregisterIdent(person.getIdenter()));
            return distPerson;
        } else {
            throw new IllegalStateException("Ukjent type for arbeidssted: " + arbeidssted.getClass().getSimpleName());
        }
    }

    private static Opplysningspliktig map(no.nav.aareg.dist.api.domain.mottak.api.v1.Opplysningspliktig opplysningspliktig) {
        if (opplysningspliktig == null) {
            return null;
        }
        if (opplysningspliktig instanceof Hovedenhet hovedenhet) {
            var distHovedenhet = new no.nav.aareg.dist.api.domain.api.v1.Hovedenhet();
            distHovedenhet.setIdent(hovedenhet.getIdent());
            return distHovedenhet;
        } else if (opplysningspliktig instanceof Person person) {
            var distPerson = new no.nav.aareg.dist.api.domain.api.v1.Person();
            distPerson.setIdent(finnGjeldendeFolkeregisterIdent(person.getIdenter()));
            return distPerson;
        } else {
            throw new IllegalStateException("Ukjent type for opplysningspliktig: " + opplysningspliktig.getClass().getSimpleName());
        }
    }

    private static Ansettelsesperiode map(no.nav.aareg.dist.api.domain.mottak.api.v1.Ansettelsesperiode ansettelsesperiode) {
        if (ansettelsesperiode == null) {
            return null;
        }
        var periode = new Ansettelsesperiode(
                map(ansettelsesperiode.getSluttaarsak()),
                map(ansettelsesperiode.getVarsling())
        );
        periode.setStartdato(ansettelsesperiode.getStartdato());
        periode.setSluttdato(ansettelsesperiode.getSluttdato());
        return periode;
    }

    private static Ansettelsesdetaljer map(no.nav.aareg.dist.api.domain.mottak.api.v1.Ansettelsesdetaljer ansettelsesdetaljer) {
        switch (ansettelsesdetaljer) {
        case OrdinaerAnsettelsesdetaljer ordinaereDetaljer -> {
            var distDetaljer = new no.nav.aareg.dist.api.domain.api.v1.OrdinaerAnsettelsesdetaljer();
            addAll(distDetaljer, ordinaereDetaljer);
            return distDetaljer;
        }
        case FrilanserAnsettelsesdetaljer frilansDetaljer -> {
            var distDetaljer = new no.nav.aareg.dist.api.domain.api.v1.FrilanserAnsettelsesdetaljer();
            addAll(distDetaljer, frilansDetaljer);
            return distDetaljer;
        }
        case ForenkletAnsettelsesdetaljer forenkletDetaljer -> {
            var distDetaljer = new no.nav.aareg.dist.api.domain.api.v1.ForenkletAnsettelsesdetaljer();
            addAll(distDetaljer, forenkletDetaljer);
            return distDetaljer;
        }
        case MaritimAnsettelsesdetaljer maritimeDetaljer -> {
            var distDetaljer = new no.nav.aareg.dist.api.domain.api.v1.MaritimAnsettelsesdetaljer();
            addAll(distDetaljer, maritimeDetaljer);
            distDetaljer.setFartoeystype(map(maritimeDetaljer.getFartoeystype()));
            distDetaljer.setFartsomraade(map(maritimeDetaljer.getFartsomraade()));
            distDetaljer.setSkipsregister(map(maritimeDetaljer.getSkipsregister()));
            return distDetaljer;
        }
        case null -> {
            return null;
        }
        default -> throw new IllegalStateException("Ukjent type for ansettelsesdetalj: " + ansettelsesdetaljer.getType().getClass());
        }
    }

    private static void addAll(Ansettelsesdetaljer distDetaljer, no.nav.aareg.dist.api.domain.mottak.api.v1.Ansettelsesdetaljer detaljer) {
        distDetaljer.setAnsettelsesform(map(detaljer.getAnsettelsesform()));
        distDetaljer.setArbeidstidsordning(map(detaljer.getArbeidstidsordning()));
        distDetaljer.setYrke(map(detaljer.getYrke()));
        distDetaljer.setRapporteringsmaaneder(map(detaljer.getRapporteringsmaaneder()));
        distDetaljer.setAvtaltStillingsprosent(detaljer.getAvtaltStillingsprosent());
        distDetaljer.setAntallTimerPrUke(detaljer.getAntallTimerPrUke());
        distDetaljer.setSisteLoennsendring(detaljer.getSisteLoennsendring());
        distDetaljer.setSisteStillingsprosentendring(detaljer.getSisteStillingsprosentendring());
    }

    private static Rapporteringsmaaneder map(no.nav.aareg.dist.api.domain.mottak.api.v1.Rapporteringsmaaneder rapporteringsmaaneder) {
        if (rapporteringsmaaneder == null) {
            return null;
        }
        return new Rapporteringsmaaneder(rapporteringsmaaneder.getFra(), rapporteringsmaaneder.getTil());
    }

    private static Permisjon map(no.nav.aareg.dist.api.domain.mottak.api.v1.Permisjon permisjon) {
        if (permisjon == null) {
            return null;
        }
        var distPermisjon = new Permisjon();
        addAll(distPermisjon, permisjon);
        return distPermisjon;
    }

    private static void addAll(PermisjonPermittering distPermisjonPermittering, no.nav.aareg.dist.api.domain.mottak.api.v1.PermisjonPermittering permisjonPermittering) {
        distPermisjonPermittering.setId(permisjonPermittering.getId());
        distPermisjonPermittering.setProsent(permisjonPermittering.getProsent());
        distPermisjonPermittering.setType(map(permisjonPermittering.getType()));
        distPermisjonPermittering.setVarsling(map(permisjonPermittering.getVarsling()));
        distPermisjonPermittering.setIdHistorikk(mapList(AaregDistMottakApiV1ToAaregDistApiV1Mapper::map, permisjonPermittering.getIdHistorikk()));
        distPermisjonPermittering.setStartdato(permisjonPermittering.getStartdato());
        distPermisjonPermittering.setSluttdato(permisjonPermittering.getSluttdato());
    }

    private static Permittering map(no.nav.aareg.dist.api.domain.mottak.api.v1.Permittering permittering) {
        if (permittering == null) {
            return null;
        }
        var distPermittering = new Permittering();
        addAll(distPermittering, permittering);
        return distPermittering;
    }

    private static TimerMedTimeloenn map(no.nav.aareg.dist.api.domain.mottak.api.v1.TimerMedTimeloenn timerMedTimeloenn) {
        if (timerMedTimeloenn == null) {
            return null;
        }
        var distTimerMedTimeloenn = new TimerMedTimeloenn(timerMedTimeloenn.getAntall(), timerMedTimeloenn.getRapporteringsmaaned().toYearMonth());
        distTimerMedTimeloenn.setStartdato(timerMedTimeloenn.getStartdato());
        distTimerMedTimeloenn.setSluttdato(timerMedTimeloenn.getSluttdato());
        return distTimerMedTimeloenn;
    }

    private static Utenlandsopphold map(no.nav.aareg.dist.api.domain.mottak.api.v1.Utenlandsopphold utenlandsopphold) {
        if (utenlandsopphold == null) {
            return null;
        }
        var distUtenlandsopphold = new Utenlandsopphold(map(utenlandsopphold.getLand()), utenlandsopphold.getRapporteringsmaaned().toYearMonth());
        distUtenlandsopphold.setStartdato(utenlandsopphold.getStartdato());
        distUtenlandsopphold.setSluttdato(utenlandsopphold.getSluttdato());
        return distUtenlandsopphold;
    }

    private static IdHistorikk map(no.nav.aareg.dist.api.domain.mottak.api.v1.IdHistorikk idHistorikk) {
        if (idHistorikk == null) {
            return null;
        }
        return new IdHistorikk(idHistorikk.getId());
    }

    private static Varsel map(no.nav.aareg.dist.api.domain.mottak.api.v1.Varsel varsel) {
        if (varsel == null) {
            return null;
        }
        return new Varsel(map(varsel.getEntitet()), map(varsel.getVarsling()));
    }

    private static Varselentitet map(no.nav.aareg.dist.api.domain.mottak.api.v1.Varselentitet entitet) {
        if (entitet == null) {
            return null;
        }
        return switch (entitet) {
            case Arbeidsforhold -> Varselentitet.Arbeidsforhold;
            case Ansettelsesperiode -> Varselentitet.Ansettelsesperiode;
            case Permisjon -> Varselentitet.Permisjon;
            case Permittering -> Varselentitet.Permittering;
        };
    }

    private static Arbeidstaker map(no.nav.aareg.dist.api.domain.mottak.api.v1.Arbeidstaker arbeidstaker) {
        if (arbeidstaker == null) {
            return null;
        }
        return new Arbeidstaker(finnGjeldendeFolkeregisterIdent(arbeidstaker.getIdenter()));
    }

    private static Kodeverksentitet map(no.nav.aareg.dist.api.domain.mottak.api.v1.Kodeverksentitet kodeverksentitet) {
        if (kodeverksentitet == null) {
            return null;
        }
        return new Kodeverksentitet(kodeverksentitet.getKode(), kodeverksentitet.getBeskrivelse());
    }

    private static <T, R> List<R> mapList(Function<T, R> mapper, List<T> entiteter) {
        if (entiteter == null) {
            return null;
        }
        return entiteter.stream().map(mapper).collect(Collectors.toList());
    }
}