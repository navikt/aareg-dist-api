package no.nav.aareg.dist.api.domain;

import no.nav.aareg.dist.api.testdata.ArbeidsforholdDistMottakTestdataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static no.nav.aareg.dist.api.util.AaregDistApiAssertUtil.assertArbeidsforhold;

class AaregApiV2ToAaregDistApiV1MapperTest {

    private final ArbeidsforholdDistMottakTestdataBuilder testdataBuilder = new ArbeidsforholdDistMottakTestdataBuilder();

    @Test
    @DisplayName("Skal mappe ordinært arbeidsforhold")
    void scenario1() {
        var aaregArbeidsforhold = testdataBuilder.defaultOrdinaertArbeidsforhold().build();

        var distApiArbeidsforhold = AaregDistMottakApiV1ToAaregDistApiV1Mapper.map(aaregArbeidsforhold);

        assertArbeidsforhold(aaregArbeidsforhold, distApiArbeidsforhold);
    }

    @Test
    @DisplayName("Skal mappe maritimt arbeidsforhold")
    void scenario2() {
        var aaregArbeidsforhold = testdataBuilder.defaultMaritimtArbeidsforhold().build();

        var distApiArbeidsforhold = AaregDistMottakApiV1ToAaregDistApiV1Mapper.map(aaregArbeidsforhold);

        assertArbeidsforhold(aaregArbeidsforhold, distApiArbeidsforhold);
    }

    @Test
    @DisplayName("Skal mappe forenklet oppgjørsordning")
    void scenario3() {
        var aaregArbeidsforhold = testdataBuilder.defaultForenkletOppgjoersordning().build();

        var distApiArbeidsforhold = AaregDistMottakApiV1ToAaregDistApiV1Mapper.map(aaregArbeidsforhold);

        assertArbeidsforhold(aaregArbeidsforhold, distApiArbeidsforhold);
    }

    @Test
    @DisplayName("Skal mappe frilanser arbeidsforhold")
    void scenario4() {
        var aaregArbeidsforhold = testdataBuilder.defaultFrilanserArbeidsforhold().build();

        var distApiArbeidsforhold = AaregDistMottakApiV1ToAaregDistApiV1Mapper.map(aaregArbeidsforhold);

        assertArbeidsforhold(aaregArbeidsforhold, distApiArbeidsforhold);
    }
}
