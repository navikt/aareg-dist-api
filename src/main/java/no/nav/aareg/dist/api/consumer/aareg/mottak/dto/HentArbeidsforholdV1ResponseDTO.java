package no.nav.aareg.dist.api.consumer.aareg.mottak.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import no.nav.aareg.dist.api.domain.mottak.api.v1.Arbeidsforhold;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HentArbeidsforholdV1ResponseDTO {

    public List<Arbeidsforhold> arbeidsforhold;
}
