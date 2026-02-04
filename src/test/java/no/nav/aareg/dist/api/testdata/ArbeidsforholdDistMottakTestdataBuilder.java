package no.nav.aareg.dist.api.testdata;

import no.nav.aareg.dist.api.domain.mottak.api.v1.Ansettelsesdetaljer;
import no.nav.aareg.dist.api.domain.mottak.api.v1.Ansettelsesperiode;
import no.nav.aareg.dist.api.domain.mottak.api.v1.Arbeidsforhold;
import no.nav.aareg.dist.api.domain.mottak.api.v1.Arbeidssted;
import no.nav.aareg.dist.api.domain.mottak.api.v1.Arbeidstaker;
import no.nav.aareg.dist.api.domain.mottak.api.v1.ForenkletAnsettelsesdetaljer;
import no.nav.aareg.dist.api.domain.mottak.api.v1.FrilanserAnsettelsesdetaljer;
import no.nav.aareg.dist.api.domain.mottak.api.v1.Hovedenhet;
import no.nav.aareg.dist.api.domain.mottak.api.v1.IdHistorikk;
import no.nav.aareg.dist.api.domain.mottak.api.v1.Ident;
import no.nav.aareg.dist.api.domain.mottak.api.v1.Kodeverksentitet;
import no.nav.aareg.dist.api.domain.mottak.api.v1.MaritimAnsettelsesdetaljer;
import no.nav.aareg.dist.api.domain.mottak.api.v1.Opplysningspliktig;
import no.nav.aareg.dist.api.domain.mottak.api.v1.OrdinaerAnsettelsesdetaljer;
import no.nav.aareg.dist.api.domain.mottak.api.v1.Permisjon;
import no.nav.aareg.dist.api.domain.mottak.api.v1.Permittering;
import no.nav.aareg.dist.api.domain.mottak.api.v1.PersistentYearMonth;
import no.nav.aareg.dist.api.domain.mottak.api.v1.Rapporteringsmaaneder;
import no.nav.aareg.dist.api.domain.mottak.api.v1.Sporingsinformasjon;
import no.nav.aareg.dist.api.domain.mottak.api.v1.TimerMedTimeloenn;
import no.nav.aareg.dist.api.domain.mottak.api.v1.Underenhet;
import no.nav.aareg.dist.api.domain.mottak.api.v1.Utenlandsopphold;
import no.nav.aareg.dist.api.domain.mottak.api.v1.Varsel;
import no.nav.aareg.dist.api.domain.mottak.api.v1.Varselentitet;
import org.assertj.core.util.VisibleForTesting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static java.util.UUID.randomUUID;
import static no.nav.aareg.dist.api.domain.dto.Arbeidsforholdtype.FORENKLET_OPPGJOERSORDNING;
import static no.nav.aareg.dist.api.domain.dto.Arbeidsforholdtype.FRILANSER_ARBEIDSFORHOLD;
import static no.nav.aareg.dist.api.domain.dto.Arbeidsforholdtype.MARITIMT_ARBEIDSFORHOLD;
import static no.nav.aareg.dist.api.domain.dto.Arbeidsforholdtype.ORDINAERT_ARBEIDSFORHOLD;
import static no.nav.aareg.dist.api.domain.mottak.api.v1.Identtype.AKTORID;
import static no.nav.aareg.dist.api.domain.mottak.api.v1.Identtype.FOLKEREGISTERIDENT;
import static no.nav.aareg.dist.api.domain.mottak.api.v1.Identtype.ORGANISASJONSNUMMER;
import static no.nav.aareg.dist.api.domain.mottak.api.v1.Rapporteringsordning.A_ORDNINGEN;
import static no.nav.aareg.dist.api.graphql.utils.AaregCollectors.toSingleton;

public class ArbeidsforholdDistMottakTestdataBuilder extends ArbeidsforholdTestdataBuilder {

    public static final LocalDate DEFAULT_START_DATO = LocalDate.of(2015, 1, 1);

    private Arbeidsforhold arbeidsforhold;

    public ArbeidsforholdDistMottakTestdataBuilder defaultArbeidsforhold() {
        this.arbeidsforhold = Arbeidsforhold.builder()
                .navArbeidsforholdId(1L)
                .navVersjon(1)
                .id(ARBEIDSFORHOLD_ID)
                .arbeidstaker(defaultArbeidstaker())
                .arbeidssted(
                        Underenhet.builder()
                                .identer(List.of(Ident.builder().type(ORGANISASJONSNUMMER).ident(ARBEIDSSTED_ID).build()))
                                .build()
                )
                .opplysningspliktig(
                        Hovedenhet.builder()
                                .identer(List.of(Ident.builder().type(ORGANISASJONSNUMMER).ident(OPPLYSNINGSPLIKTIG_ID).build()))
                                .build()
                )
                .type(Kodeverksentitet.builder().kode(ORDINAERT_ARBEIDSFORHOLD).build())
                .ansettelsesperiode(defaultAnsettelsesperiode(DEFAULT_START_DATO, null))
                .ansettelsesdetaljer(List.of(defaultAnsettelsesdetaljer(DEFAULT_START_DATO, null)))
                .permisjoner(List.of(defaultPermisjon()))
                .permitteringer(List.of(defaultPermittering()))
                .timerMedTimeloenn(List.of(defaultTimerMedTimeloenn()))
                .utenlandsopphold(List.of(defaultUtenlandsopphold()))
                .idHistorikk(List.of(IdHistorikk.builder().id("cab-321").build()))
                .varsler(List.of(Varsel.builder().entitet(Varselentitet.Arbeidsforhold).varsling(Kodeverksentitet.builder().kode("varslingskode").build()).build()))
                .rapporteringsordning(Kodeverksentitet.builder().kode(A_ORDNINGEN.name()).build())
                .navUuid(randomUUID().toString())
                .opprettet(DEFAULT_START_DATO.atStartOfDay())
                .sistBekreftet(DEFAULT_START_DATO.atStartOfDay())
                .sistEndret(DEFAULT_START_DATO.atStartOfDay())
                .sporingsinformasjon(defaultSporingsinformasjon())
                .build();
        return this;
    }

    public ArbeidsforholdDistMottakTestdataBuilder defaultOrdinaertArbeidsforhold() {
        return defaultArbeidsforhold();
    }

    public ArbeidsforholdDistMottakTestdataBuilder defaultMaritimtArbeidsforhold() {
        return defaultArbeidsforhold()
                .withArbeidsforholdType(MARITIMT_ARBEIDSFORHOLD)
                .withAnsettelsesdetaljer(List.of(defaultMaritimAnsettelsesdetaljer()), true);
    }

    public ArbeidsforholdDistMottakTestdataBuilder defaultForenkletOppgjoersordning() {
        return defaultArbeidsforhold()
                .withArbeidsforholdType(FORENKLET_OPPGJOERSORDNING)
                .withAnsettelsesdetaljer(List.of(defaultForenkletAnsettelsesdetaljer()), true)
                .withoutPermisjoner()
                .withoutPermitteringer()
                .withoutTimerMedTimeloenn()
                .withoutUtenlandsopphold();
    }

    public ArbeidsforholdDistMottakTestdataBuilder defaultFrilanserArbeidsforhold() {
        return defaultArbeidsforhold()
                .withArbeidsforholdType(FRILANSER_ARBEIDSFORHOLD)
                .withAnsettelsesdetaljer(List.of(defaultFrilanserAnsettelsesdetaljer()), true)
                .withoutPermisjoner()
                .withoutPermitteringer()
                .withoutTimerMedTimeloenn()
                .withoutUtenlandsopphold();
    }

    public ArbeidsforholdDistMottakTestdataBuilder withArbeidstaker(String arbeidstakerId) {
        this.arbeidsforhold.setArbeidstaker(Arbeidstaker.builder()
                .identer(
                        List.of(
                                Ident.builder().type(AKTORID).ident(ARBEIDSTAKER_AKTOER_ID).gjeldende(true).build(),
                                Ident.builder().type(FOLKEREGISTERIDENT).ident(arbeidstakerId).gjeldende(true).build()
                        )
                )
                .build());
        return this;
    }

    public ArbeidsforholdDistMottakTestdataBuilder withArbeidstaker(Arbeidstaker arbeidstaker) {
        this.arbeidsforhold.setArbeidstaker(arbeidstaker);
        return this;
    }

    public ArbeidsforholdDistMottakTestdataBuilder withArbeidssted(String arbeidsstedId) {
        this.arbeidsforhold.setArbeidssted(Underenhet.builder().identer(List.of(Ident.builder().type(ORGANISASJONSNUMMER).ident(arbeidsstedId).gjeldende(TRUE).build())).build());
        return this;
    }

    public ArbeidsforholdDistMottakTestdataBuilder withArbeidssted(Arbeidssted arbeidssted) {
        this.arbeidsforhold.setArbeidssted(arbeidssted);
        return this;
    }

    public ArbeidsforholdDistMottakTestdataBuilder withOpplysningspliktig(String opplysningspliktigId) {
        this.arbeidsforhold.setOpplysningspliktig(Hovedenhet.builder().identer(List.of(Ident.builder().type(ORGANISASJONSNUMMER).ident(opplysningspliktigId).gjeldende(TRUE).build())).build());
        return this;
    }

    public ArbeidsforholdDistMottakTestdataBuilder withOpplysningspliktig(Opplysningspliktig opplysningspliktig) {
        this.arbeidsforhold.setOpplysningspliktig(opplysningspliktig);
        return this;
    }

    public ArbeidsforholdDistMottakTestdataBuilder withStartdato(LocalDate startdato) {
        this.arbeidsforhold.getAnsettelsesperiode().setStartdato(startdato);
        this.arbeidsforhold.getAnsettelsesdetaljer().stream().collect(toSingleton()).getRapporteringsmaaneder()
                .setFraAsString(YearMonth.of(startdato.getYear(), startdato.getMonthValue()).toString());
        this.arbeidsforhold.setOpprettet(startdato.atStartOfDay());
        this.arbeidsforhold.setSistEndret(startdato.atStartOfDay());
        this.arbeidsforhold.setSistBekreftet(startdato.atStartOfDay());
        return this;
    }

    public ArbeidsforholdDistMottakTestdataBuilder withSluttdato(LocalDate sluttdato) {
        this.arbeidsforhold.getAnsettelsesperiode().setSluttdato(sluttdato);
        return this;
    }

    public ArbeidsforholdDistMottakTestdataBuilder withId(String id) {
        this.arbeidsforhold.setId(id);
        return this;
    }

    public ArbeidsforholdDistMottakTestdataBuilder withRapporteringsordning(Kodeverksentitet rapporteringsordning) {
        this.arbeidsforhold.setRapporteringsordning(rapporteringsordning);
        return this;
    }

    public ArbeidsforholdDistMottakTestdataBuilder withAnsettelsesdetaljer(List<Ansettelsesdetaljer> ansettelsesdetaljer, boolean replaceDefault) {
        if (!replaceDefault) {
            this.arbeidsforhold.getAnsettelsesdetaljer().stream().findFirst().ifPresent(ansettelsesdetaljer::add);
        }
        this.arbeidsforhold.setAnsettelsesdetaljer(ansettelsesdetaljer);
        return this;
    }

    public ArbeidsforholdDistMottakTestdataBuilder withPermisjoner(List<Permisjon> permisjoner) {
        this.arbeidsforhold.setPermisjoner(permisjoner);
        return this;
    }

    public ArbeidsforholdDistMottakTestdataBuilder withPermitteringer(List<Permittering> permitteringer) {
        this.arbeidsforhold.setPermitteringer(permitteringer);
        return this;
    }

    public ArbeidsforholdDistMottakTestdataBuilder withTimerMedTimerLoenn(List<TimerMedTimeloenn> timerMedTimerLoenn) {
        this.arbeidsforhold.setTimerMedTimeloenn(timerMedTimerLoenn);
        return this;
    }

    public ArbeidsforholdDistMottakTestdataBuilder withUtenlandsopphold(List<Utenlandsopphold> utenlandsopphold) {
        this.arbeidsforhold.setUtenlandsopphold(utenlandsopphold);
        return this;
    }

    public ArbeidsforholdDistMottakTestdataBuilder withVarsler(List<Varsel> varsler) {
        this.arbeidsforhold.setVarsler(varsler);
        return this;
    }

    public ArbeidsforholdDistMottakTestdataBuilder withoutPermisjoner() {
        this.arbeidsforhold.setPermisjoner(null);
        return this;
    }

    public ArbeidsforholdDistMottakTestdataBuilder withoutPermitteringer() {
        this.arbeidsforhold.setPermitteringer(null);
        return this;
    }

    public ArbeidsforholdDistMottakTestdataBuilder withoutTimerMedTimeloenn() {
        this.arbeidsforhold.setTimerMedTimeloenn(null);
        return this;
    }

    public ArbeidsforholdDistMottakTestdataBuilder withoutUtenlandsopphold() {
        this.arbeidsforhold.setUtenlandsopphold(null);
        return this;
    }

    public Arbeidsforhold build() {
        return arbeidsforhold;
    }

    private Arbeidstaker defaultArbeidstaker() {
        return Arbeidstaker.builder()
                .identer(
                        List.of(
                                Ident.builder().type(FOLKEREGISTERIDENT).ident(ARBEIDSTAKER_FNR).gjeldende(true).build(),
                                Ident.builder().type(AKTORID).ident(ARBEIDSTAKER_AKTOER_ID).gjeldende(true).build()
                        )
                )
                .build();
    }

    private Ansettelsesperiode defaultAnsettelsesperiode(LocalDate fom, LocalDate tom) {
        return Ansettelsesperiode.builder()
                .gjeldende(true)
                .startdato(fom)
                .sluttdato(tom)
                .sluttaarsak(null)
                .sporingsinformasjon(Sporingsinformasjon.builder()
                        .endretAv("A")
                        .endretKilde("B")
                        .endretKildereferanse("C")
                        .endretTidspunkt(LocalDateTime.now())
                        .opprettetAv("D")
                        .opprettetKilde("E")
                        .opprettetKildereferanse("F")
                        .opprettetTidspunkt(LocalDateTime.now())
                        .build())
                .varsling(null)
                .build();
    }

    public Ansettelsesdetaljer defaultAnsettelsesdetaljer(LocalDate fom, LocalDate tom) {
        return OrdinaerAnsettelsesdetaljer.builder()
                .ansettelsesform(Kodeverksentitet.builder().kode("A").build())
                .antallTimerPrUke(37.5)
                .arbeidstidsordning(Kodeverksentitet.builder().kode("fast").build())
                .rapporteringsmaaneder(Rapporteringsmaaneder.builder()
                        .fra(new PersistentYearMonth(fom.getYear(), fom.getMonthValue()))
                        .til(tom != null ? new PersistentYearMonth(tom.getYear(), tom.getMonthValue()) : null)
                        .build())
                .sisteLoennsendring(LocalDate.of(2015, 1, 1))
                .sporingsinformasjon(defaultSporingsinformasjon())
                .sisteStillingsprosentendring(LocalDate.of(2015, 1, 1))
                .avtaltStillingsprosent(100.0)
                .yrke(Kodeverksentitet.builder().kode("6411126").beskrivelse("UTVIKLER").build())
                .build();
    }

    public Ansettelsesdetaljer defaultMaritimAnsettelsesdetaljer() {
        YearMonth now = YearMonth.now();
        return MaritimAnsettelsesdetaljer.builder()
                .fartoeystype(Kodeverksentitet.builder().kode("fartoeystype").build())
                .skipsregister(Kodeverksentitet.builder().kode("skipsregister").build())
                .fartsomraade(Kodeverksentitet.builder().kode("fartsomraade").build())
                .ansettelsesform(Kodeverksentitet.builder().kode("ansettelsesform").build())
                .arbeidstidsordning(Kodeverksentitet.builder().kode("arbeidstidsordning").build())
                .yrke(Kodeverksentitet.builder().kode("yrke").build())
                .antallTimerPrUke(37.5d)
                .avtaltStillingsprosent(100.0d)
                .sisteStillingsprosentendring(LocalDate.now())
                .sisteLoennsendring(LocalDate.now())
                .rapporteringsmaaneder(Rapporteringsmaaneder.builder().fra(new PersistentYearMonth(now.getYear(), now.getMonthValue())).build())
                .build();
    }

    public Ansettelsesdetaljer defaultForenkletAnsettelsesdetaljer() {
        YearMonth now = YearMonth.now();
        return ForenkletAnsettelsesdetaljer.builder()
                .yrke(Kodeverksentitet.builder().kode("yrke").build())
                .rapporteringsmaaneder(Rapporteringsmaaneder.builder().fra(new PersistentYearMonth(now.getYear(), now.getMonthValue())).build())
                .build();
    }

    public Ansettelsesdetaljer defaultFrilanserAnsettelsesdetaljer() {
        YearMonth now = YearMonth.now();
        return FrilanserAnsettelsesdetaljer.builder()
                .yrke(Kodeverksentitet.builder().kode("yrke").build())
                .antallTimerPrUke(37.5d)
                .rapporteringsmaaneder(Rapporteringsmaaneder.builder().fra(new PersistentYearMonth(now.getYear(), now.getMonthValue())).build())
                .build();
    }

    public Permisjon defaultPermisjon() {
        return createPermisjon(LocalDate.now(), null);
    }

    public Permisjon createPermisjon(LocalDate startdato, LocalDate sluttdato) {
        return Permisjon.builder()
                .id("permisjon-id")
                .idHistorikk(null)
                .prosent(100.0)
                .startdato(startdato)
                .sluttdato(sluttdato)
                .sporingsinformasjon(defaultSporingsinformasjon())
                .type(Kodeverksentitet.builder().kode("permisjon").build())
                .build();
    }

    public Permittering defaultPermittering() {
        return createPermittering(LocalDate.now(), null);
    }

    public Permittering createPermittering(LocalDate startdato, LocalDate sluttdato) {
        return Permittering.builder()
                .id("permittering-id")
                .idHistorikk(null)
                .prosent(100.0)
                .startdato(startdato)
                .sluttdato(sluttdato)
                .sporingsinformasjon(defaultSporingsinformasjon())
                .type(Kodeverksentitet.builder().kode("permittering").build())
                .build();
    }

    public TimerMedTimeloenn defaultTimerMedTimeloenn() {
        return createTimerMedTimeloenn(new PersistentYearMonth(), LocalDate.now(), null);
    }

    public TimerMedTimeloenn createTimerMedTimeloenn(PersistentYearMonth rapporteringsMaaned, LocalDate startdato, LocalDate sluttdato) {
        return TimerMedTimeloenn.builder()
                .antall(10.0)
                .rapporteringsmaaned(rapporteringsMaaned)
                .startdato(startdato)
                .sluttdato(sluttdato)
                .sporingsinformasjon(defaultSporingsinformasjon())
                .build();
    }

    public Utenlandsopphold defaultUtenlandsopphold() {
        YearMonth now = YearMonth.now();
        return createUtenlandsopphold(new PersistentYearMonth(), LocalDate.now(), null);
    }

    private Sporingsinformasjon defaultSporingsinformasjon() {
        return Sporingsinformasjon.builder()
                .endretAv("A")
                .endretKilde("B")
                .endretKildereferanse("C")
                .endretTidspunkt(LocalDateTime.now())
                .opprettetAv("D")
                .opprettetKilde("E")
                .opprettetKildereferanse("F")
                .opprettetTidspunkt(LocalDateTime.now())
                .build();
    }

    public Utenlandsopphold createUtenlandsopphold(PersistentYearMonth rapporteringsMaaned, LocalDate startdato, LocalDate sluttdato) {
        return Utenlandsopphold.builder()
                .land(Kodeverksentitet.builder().kode("DK").build())
                .rapporteringsmaaned(rapporteringsMaaned)
                .startdato(startdato)
                .sluttdato(sluttdato)
                .sporingsinformasjon(defaultSporingsinformasjon())
                .build();
    }

    public Ansettelsesdetaljer createAnsettelsesdetaljer(LocalDate start, LocalDate slutt) {
        return OrdinaerAnsettelsesdetaljer.builder()
                .ansettelsesform(Kodeverksentitet.builder().kode("A").build())
                .antallTimerPrUke(37.5)
                .arbeidstidsordning(Kodeverksentitet.builder().kode("fast").build())
                .rapporteringsmaaneder(Rapporteringsmaaneder.builder()
                        .fra(new PersistentYearMonth(start.getYear(), start.getMonthValue()))
                        .til(slutt != null ? new PersistentYearMonth(slutt.getYear(), slutt.getMonthValue()) : null)
                        .build())
                .sisteLoennsendring(LocalDate.of(2015, 1, 1))
                .sporingsinformasjon(defaultSporingsinformasjon())
                .sisteStillingsprosentendring(LocalDate.of(2015, 1, 1))
                .avtaltStillingsprosent(100.0)
                .yrke(Kodeverksentitet.builder().kode("6411126").build())
                .build();
    }

    public ArbeidsforholdDistMottakTestdataBuilder withArbeidsforholdType(String type) {
        this.arbeidsforhold.setType(Kodeverksentitet.builder().kode(type).build());
        return this;
    }

    @VisibleForTesting
    public ArbeidsforholdDistMottakTestdataBuilder withoutOpprettet() {
        this.arbeidsforhold.setOpprettet(null);
        return this;
    }

    @VisibleForTesting
    public ArbeidsforholdDistMottakTestdataBuilder withoutSistBekreftet() {
        this.arbeidsforhold.setSistBekreftet(null);
        return this;
    }
}
