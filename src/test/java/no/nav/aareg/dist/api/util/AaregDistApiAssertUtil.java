package no.nav.aareg.dist.api.util;

import java.util.List;
import java.util.Objects;

import static no.nav.aareg.dist.api.graphql.utils.AaregCollectors.toSingleton;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.util.CollectionUtils.isEmpty;

public class AaregDistApiAssertUtil {

    public static void assertArbeidsforhold(
            no.nav.aareg.dist.api.domain.mottak.api.v1.Arbeidsforhold expectedArbeidsforhold,
            no.nav.aareg.dist.api.domain.api.v1.Arbeidsforhold actualArbeidsforhold
    ) {
        assertAll(
                () -> assertEquals(expectedArbeidsforhold.getId(), actualArbeidsforhold.getId()),
                () -> assertKodeverksentitet(expectedArbeidsforhold.getType(), actualArbeidsforhold.getType()),
                () -> assertNotNull(actualArbeidsforhold.getOpplysningspliktig()),
                () -> assertNotNull(actualArbeidsforhold.getArbeidssted()),
                () -> assertNotNull(actualArbeidsforhold.getArbeidstaker()),
                () -> assertAnsettelsesperiode(expectedArbeidsforhold.getAnsettelsesperiode(), actualArbeidsforhold.getAnsettelsesperiode()),
                () -> assertAnsettelsesdetaljer(expectedArbeidsforhold.getAnsettelsesdetaljer(), actualArbeidsforhold.getAnsettelsesdetaljer()),
                () -> assertPermisjonPermitteringer(expectedArbeidsforhold.getPermisjoner(), actualArbeidsforhold.getPermisjoner()),
                () -> assertPermisjonPermitteringer(expectedArbeidsforhold.getPermitteringer(), actualArbeidsforhold.getPermitteringer()),
                () -> assertTimerMedTimeloennliste(expectedArbeidsforhold.getTimerMedTimeloenn(), actualArbeidsforhold.getTimerMedTimeloenn()),
                () -> assertUtenlandsoppholdliste(expectedArbeidsforhold.getUtenlandsopphold(), actualArbeidsforhold.getUtenlandsopphold()),
                () -> assertVarsler(expectedArbeidsforhold.getVarsler(), actualArbeidsforhold.getVarsler()),
                () -> assertIdHistorikkliste(expectedArbeidsforhold.getIdHistorikk(), actualArbeidsforhold.getIdHistorikk()),
                () -> assertKodeverksentitet(expectedArbeidsforhold.getRapporteringsordning(), actualArbeidsforhold.getRapporteringsordning()),
                () -> assertEquals(expectedArbeidsforhold.getNavUuid(), actualArbeidsforhold.getUuid()),
                () -> assertEquals(expectedArbeidsforhold.getOpprettet(), actualArbeidsforhold.getOpprettet()),
                () -> assertEquals(expectedArbeidsforhold.getSistEndret(), actualArbeidsforhold.getSistEndret()),
                () -> assertEquals(expectedArbeidsforhold.getSistBekreftet(), actualArbeidsforhold.getSistBekreftet())
        );
    }

    private static void assertAnsettelsesperiode(
            no.nav.aareg.dist.api.domain.mottak.api.v1.Ansettelsesperiode expectedAnsettelsesperiode,
            no.nav.aareg.dist.api.domain.api.v1.Ansettelsesperiode actualAnsettelsesperiode
    ) {
        assertAll(
                () -> assertEquals(expectedAnsettelsesperiode.getStartdato(), actualAnsettelsesperiode.getStartdato()),
                () -> assertEquals(expectedAnsettelsesperiode.getSluttdato(), actualAnsettelsesperiode.getSluttdato()),
                () -> assertKodeverksentitet(expectedAnsettelsesperiode.getSluttaarsak(), actualAnsettelsesperiode.getSluttaarsak()),
                () -> assertKodeverksentitet(expectedAnsettelsesperiode.getVarsling(), actualAnsettelsesperiode.getVarsling())
        );
    }

    private static void assertAnsettelsesdetaljer(
            List<no.nav.aareg.dist.api.domain.mottak.api.v1.Ansettelsesdetaljer> expectedAnsettelsesdetaljerliste,
            List<no.nav.aareg.dist.api.domain.api.v1.Ansettelsesdetaljer> actualAnsettelsesdetaljerliste
    ) {
        if (!isEmpty(expectedAnsettelsesdetaljerliste)) {
            assertEquals(expectedAnsettelsesdetaljerliste.size(), actualAnsettelsesdetaljerliste.size());
            actualAnsettelsesdetaljerliste.forEach(actualAnsettelsesdetaljer -> {
                var expectedAnsettelsesdetaljer = expectedAnsettelsesdetaljerliste.stream()
                        .filter(expected ->
                                Objects.equals(expected.getType(), actualAnsettelsesdetaljer.getType())
                                        && Objects.equals(expected.getRapporteringsmaaneder().getFra(), actualAnsettelsesdetaljer.getRapporteringsmaaneder().getFra())
                                        && Objects.equals(expected.getRapporteringsmaaneder().getTil(), actualAnsettelsesdetaljer.getRapporteringsmaaneder().getTil())
                        )
                        .collect(toSingleton());
                assertAnsettelsesdetaljer(expectedAnsettelsesdetaljer, actualAnsettelsesdetaljer);
            });
        } else {
            assertNull(actualAnsettelsesdetaljerliste);
        }
    }

    private static void assertAnsettelsesdetaljer(
            no.nav.aareg.dist.api.domain.mottak.api.v1.Ansettelsesdetaljer expectedAnsettelsesdetaljer,
            no.nav.aareg.dist.api.domain.api.v1.Ansettelsesdetaljer actualAnsettelsesdetaljer
    ) {
        assertAll(
                () -> assertKodeverksentitet(expectedAnsettelsesdetaljer.getAnsettelsesform(), actualAnsettelsesdetaljer.getAnsettelsesform()),
                () -> assertKodeverksentitet(expectedAnsettelsesdetaljer.getArbeidstidsordning(), actualAnsettelsesdetaljer.getArbeidstidsordning()),
                () -> assertKodeverksentitet(expectedAnsettelsesdetaljer.getYrke(), actualAnsettelsesdetaljer.getYrke()),
                () -> assertEquals(expectedAnsettelsesdetaljer.getAntallTimerPrUke(), actualAnsettelsesdetaljer.getAntallTimerPrUke()),
                () -> assertEquals(expectedAnsettelsesdetaljer.getAvtaltStillingsprosent(), actualAnsettelsesdetaljer.getAvtaltStillingsprosent()),
                () -> assertEquals(expectedAnsettelsesdetaljer.getSisteLoennsendring(), actualAnsettelsesdetaljer.getSisteLoennsendring()),
                () -> assertEquals(expectedAnsettelsesdetaljer.getSisteStillingsprosentendring(), actualAnsettelsesdetaljer.getSisteStillingsprosentendring()),
                () -> assertRapporteringsmaaneder(expectedAnsettelsesdetaljer.getRapporteringsmaaneder(), actualAnsettelsesdetaljer.getRapporteringsmaaneder())
        );

        if (actualAnsettelsesdetaljer.getType().equals(no.nav.aareg.dist.api.domain.api.v1.MaritimAnsettelsesdetaljer.TYPE)) {
            var expectedMaritimAnsettelsesdetaljer = (no.nav.aareg.dist.api.domain.mottak.api.v1.MaritimAnsettelsesdetaljer) expectedAnsettelsesdetaljer;
            var actualMaritimAnsettelsesdetaljer = (no.nav.aareg.dist.api.domain.api.v1.MaritimAnsettelsesdetaljer) actualAnsettelsesdetaljer;
            assertAll(
                    () -> assertKodeverksentitet(expectedMaritimAnsettelsesdetaljer.getFartoeystype(), actualMaritimAnsettelsesdetaljer.getFartoeystype()),
                    () -> assertKodeverksentitet(expectedMaritimAnsettelsesdetaljer.getSkipsregister(), actualMaritimAnsettelsesdetaljer.getSkipsregister()),
                    () -> assertKodeverksentitet(expectedMaritimAnsettelsesdetaljer.getFartsomraade(), actualMaritimAnsettelsesdetaljer.getFartsomraade())
            );
        }
    }

    private static void assertRapporteringsmaaneder(no.nav.aareg.dist.api.domain.mottak.api.v1.Rapporteringsmaaneder expectedRapporteringsmaaneder, no.nav.aareg.dist.api.domain.api.v1.Rapporteringsmaaneder actualRapporteringsmaaneder) {
        assertAll(
                () -> assertEquals(expectedRapporteringsmaaneder.getFra(), actualRapporteringsmaaneder.getFra()),
                () -> assertEquals(expectedRapporteringsmaaneder.getTil(), actualRapporteringsmaaneder.getTil())
        );
    }

    private static <E extends no.nav.aareg.dist.api.domain.mottak.api.v1.PermisjonPermittering, A extends no.nav.aareg.dist.api.domain.api.v1.PermisjonPermittering> void assertPermisjonPermitteringer(
            List<E> expectedPermisjonPermitteringer,
            List<A> actualPermisjonPermitteringer
    ) {
        if (!isEmpty(expectedPermisjonPermitteringer)) {
            assertEquals(expectedPermisjonPermitteringer.size(), actualPermisjonPermitteringer.size());
            actualPermisjonPermitteringer.forEach(
                    actualPermisjonPermittering -> {
                        var expectedPermisjonPermittering = expectedPermisjonPermitteringer.stream()
                                .filter(expected ->
                                        Objects.equals(expected.getType().getKode(), actualPermisjonPermittering.getType().getKode())
                                                && Objects.equals(expected.getId(), actualPermisjonPermittering.getId())
                                )
                                .collect(toSingleton());
                        assertPermisjonPermittering(expectedPermisjonPermittering, actualPermisjonPermittering);
                    }
            );
        } else {
            assertNull(actualPermisjonPermitteringer);
        }
    }

    private static <E extends no.nav.aareg.dist.api.domain.mottak.api.v1.PermisjonPermittering, A extends no.nav.aareg.dist.api.domain.api.v1.PermisjonPermittering> void assertPermisjonPermittering(
            E expectedPermisjonPermittering,
            A actualPermisjonPermittering
    ) {
        assertAll(
                () -> assertKodeverksentitet(expectedPermisjonPermittering.getType(), actualPermisjonPermittering.getType()),
                () -> assertEquals(expectedPermisjonPermittering.getId(), actualPermisjonPermittering.getId()),
                () -> assertEquals(expectedPermisjonPermittering.getProsent(), actualPermisjonPermittering.getProsent()),
                () -> assertEquals(expectedPermisjonPermittering.getStartdato(), actualPermisjonPermittering.getStartdato()),
                () -> assertEquals(expectedPermisjonPermittering.getSluttdato(), actualPermisjonPermittering.getSluttdato()),
                () -> assertIdHistorikkliste(expectedPermisjonPermittering.getIdHistorikk(), actualPermisjonPermittering.getIdHistorikk()),
                () -> assertKodeverksentitet(expectedPermisjonPermittering.getVarsling(), actualPermisjonPermittering.getVarsling())
        );
    }

    private static void assertTimerMedTimeloennliste(List<no.nav.aareg.dist.api.domain.mottak.api.v1.TimerMedTimeloenn> expectedTimerMedTimeloennliste,
                                                     List<no.nav.aareg.dist.api.domain.api.v1.TimerMedTimeloenn> actualTimerMedTimeloennliste) {
        if (!isEmpty(expectedTimerMedTimeloennliste)) {
            assertEquals(expectedTimerMedTimeloennliste.size(), actualTimerMedTimeloennliste.size());
            for (int i = 0; i < expectedTimerMedTimeloennliste.size(); i++) {
                assertTimerMedTimeloenn(expectedTimerMedTimeloennliste.get(i), actualTimerMedTimeloennliste.get(i));
            }
        } else {
            assertNull(actualTimerMedTimeloennliste);
        }
    }

    private static void assertTimerMedTimeloenn(no.nav.aareg.dist.api.domain.mottak.api.v1.TimerMedTimeloenn expectedTimerMedTimeloenn, no.nav.aareg.dist.api.domain.api.v1.TimerMedTimeloenn actualTimerMedTimeloenn) {
        assertAll(
                () -> assertEquals(expectedTimerMedTimeloenn.getAntall(), actualTimerMedTimeloenn.getAntall()),
                () -> assertEquals(expectedTimerMedTimeloenn.getStartdato(), actualTimerMedTimeloenn.getStartdato()),
                () -> assertEquals(expectedTimerMedTimeloenn.getSluttdato(), actualTimerMedTimeloenn.getSluttdato()),
                () -> assertEquals(expectedTimerMedTimeloenn.getRapporteringsmaaned().toYearMonth(), actualTimerMedTimeloenn.getRapporteringsmaaned())
        );
    }

    private static void assertUtenlandsoppholdliste(List<no.nav.aareg.dist.api.domain.mottak.api.v1.Utenlandsopphold> expectedUtenlandsoppholdliste, List<no.nav.aareg.dist.api.domain.api.v1.Utenlandsopphold> actualUtenlandsoppholdliste) {
        if (!isEmpty(expectedUtenlandsoppholdliste)) {
            assertEquals(expectedUtenlandsoppholdliste.size(), actualUtenlandsoppholdliste.size());
            for (int i = 0; i < expectedUtenlandsoppholdliste.size(); i++) {
                assertUtenlandsopphold(expectedUtenlandsoppholdliste.get(i), actualUtenlandsoppholdliste.get(i));
            }
        } else {
            assertNull(actualUtenlandsoppholdliste);
        }
    }

    private static void assertUtenlandsopphold(no.nav.aareg.dist.api.domain.mottak.api.v1.Utenlandsopphold expectedUtenlandsopphold, no.nav.aareg.dist.api.domain.api.v1.Utenlandsopphold actualUtenlandsopphold) {
        assertAll(
                () -> assertKodeverksentitet(expectedUtenlandsopphold.getLand(), actualUtenlandsopphold.getLand()),
                () -> assertEquals(expectedUtenlandsopphold.getStartdato(), actualUtenlandsopphold.getStartdato()),
                () -> assertEquals(expectedUtenlandsopphold.getSluttdato(), actualUtenlandsopphold.getSluttdato()),
                () -> assertEquals(expectedUtenlandsopphold.getRapporteringsmaaned().toYearMonth(), actualUtenlandsopphold.getRapporteringsmaaned())
        );
    }

    private static void assertIdHistorikkliste(List<no.nav.aareg.dist.api.domain.mottak.api.v1.IdHistorikk> expectedIdHistorikkliste, List<no.nav.aareg.dist.api.domain.api.v1.IdHistorikk> actualIdHistorikkliste) {
        if (!isEmpty(expectedIdHistorikkliste)) {
            assertEquals(expectedIdHistorikkliste.size(), actualIdHistorikkliste.size());
            for (int i = 0; i < expectedIdHistorikkliste.size(); i++) {
                assertEquals(expectedIdHistorikkliste.get(i).getId(), actualIdHistorikkliste.get(i).getId());
            }
        } else {
            assertNull(actualIdHistorikkliste);
        }
    }

    private static void assertVarsler(List<no.nav.aareg.dist.api.domain.mottak.api.v1.Varsel> expectedVarselliste, List<no.nav.aareg.dist.api.domain.api.v1.Varsel> actualVarselliste) {
        if (!isEmpty(expectedVarselliste)) {
            assertEquals(expectedVarselliste.size(), actualVarselliste.size());
            for (int i = 0; i < expectedVarselliste.size(); i++) {
                assertVarsel(expectedVarselliste.get(i), actualVarselliste.get(i));
            }
        } else {
            assertNull(actualVarselliste);
        }
    }

    private static void assertVarsel(no.nav.aareg.dist.api.domain.mottak.api.v1.Varsel expectedVarsel, no.nav.aareg.dist.api.domain.api.v1.Varsel actualVarsel) {
        assertAll(
                () -> assertEquals(expectedVarsel.getEntitet().name(), actualVarsel.getEntitet().name()),
                () -> assertKodeverksentitet(expectedVarsel.getVarsling(), actualVarsel.getVarsling())
        );
    }

    private static void assertKodeverksentitet(no.nav.aareg.dist.api.domain.mottak.api.v1.Kodeverksentitet expectedKodeverksentitet, no.nav.aareg.dist.api.domain.api.v1.Kodeverksentitet actualKodeverksentitet) {
        if (expectedKodeverksentitet != null) {
            assertAll(
                    () -> assertEquals(expectedKodeverksentitet.getKode(), actualKodeverksentitet.getKode()),
                    () -> assertEquals(expectedKodeverksentitet.getBeskrivelse(), actualKodeverksentitet.getBeskrivelse())
            );
        } else {
            assertNull(actualKodeverksentitet);
        }
    }
}