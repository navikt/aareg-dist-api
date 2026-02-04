package no.nav.aareg.dist.api.service;

import lombok.RequiredArgsConstructor;
import no.nav.aareg.dist.api.consumer.aareg.mottak.dto.HentArbeidsforholdRequestDTO;
import no.nav.aareg.dist.api.consumer.aareg.tilgangskontroll.AaregTilgangskontrollConsumer;
import no.nav.aareg.dist.api.consumer.aareg.tilgangskontroll.dto.Kontekst;
import no.nav.aareg.dist.api.consumer.aareg.tilgangskontroll.dto.Tilgang;
import no.nav.aareg.dist.api.consumer.aareg.tilgangskontroll.dto.TilgangsforespoerselRequest;
import no.nav.aareg.dist.api.domain.mottak.api.v1.Arbeidsforhold;
import no.nav.aareg.dist.api.domain.mottak.api.v1.Hovedenhet;
import no.nav.aareg.dist.api.domain.mottak.api.v1.Person;
import no.nav.aareg.dist.api.domain.mottak.api.v1.Underenhet;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static no.nav.aareg.dist.api.domain.mottak.api.v1.Identtype.FOLKEREGISTERIDENT;

@Component
@RequiredArgsConstructor
public class TilgangskontrollService {

    private final AaregTilgangskontrollConsumer tilgangskontrollConsumer;

    public List<Tilgang> hentTilganger(List<Arbeidsforhold> arbeidsforholdliste, HentArbeidsforholdRequestDTO hentArbeidsforholdRequest) {
        var forespurteArbeidstakere = new HashSet<TilgangsforespoerselRequest.Arbeidstaker>();

        if (arbeidsforholdliste.isEmpty()) {
            forespurteArbeidstakere.add(new TilgangsforespoerselRequest.Arbeidstaker(hentArbeidsforholdRequest.getOpplysningspliktigId(), hentArbeidsforholdRequest.getArbeidsstedId(), hentArbeidsforholdRequest.getArbeidstakerId()));
        } else {
            forespurteArbeidstakere.addAll(arbeidsforholdliste.stream()
                    .map(this::hentArbeidstaker)
                    .collect(Collectors.toSet())
            );
        }

        var tilgangsforespoerselRequest = new TilgangsforespoerselRequest(Kontekst.SYSTEM_UTEN_TILGANG_TIL_ADRESSEBESKYTTELSE, TilgangsforespoerselRequest.Operasjon.LESE, forespurteArbeidstakere);
        var tilgangskontrollResposne = tilgangskontrollConsumer.kontrollerTilganger(tilgangsforespoerselRequest);

        var tilganger = tilgangskontrollResposne.tilganger();

        if (tilganger.isEmpty()) {
            throw new IllegalStateException("Fant ingen tilganger");
        }

        return tilganger;
    }

    public List<Arbeidsforhold> filtrerArbeidsforhold(List<Arbeidsforhold> arbeidsforholdliste, List<Tilgang> tilganger) {
        return arbeidsforholdliste.stream()
                .filter(arbeidsforhold ->  harTilgang(tilganger, arbeidsforhold))
                .toList();
    }

    private boolean harTilgang(List<Tilgang> tilganger, Arbeidsforhold arbeidsforhold) {
        var arbeidstaker = hentArbeidstaker(arbeidsforhold);
        var targetTilgang = tilganger.stream()
                .filter(t -> t.opplysningspliktigIdentifikator().equals(arbeidstaker.opplysningspliktigIdentifikator())
                        && t.arbeidsstedIdentifikator().equals(arbeidstaker.arbeidsstedIdentifikator())
                        && t.arbeidstakerIdentifkator().equals(arbeidstaker.arbeidstakerIdentifikator()))
                .findFirst()
                .orElse(null);

        if (targetTilgang == null) {
            return false;
        } else {
            return targetTilgang.harTilgang();
        }
    }

    private TilgangsforespoerselRequest.Arbeidstaker hentArbeidstaker(Arbeidsforhold arbeidsforhold) {
        var opplysningspliktigId = switch (arbeidsforhold.getOpplysningspliktig()) {
            case Person person -> person.getIdent(FOLKEREGISTERIDENT);
            case Hovedenhet hovedenhet -> hovedenhet.getIdent();
            default -> throw new IllegalStateException("Unexpected value: " + arbeidsforhold.getOpplysningspliktig());
        };

        var arbeidsstedId = switch (arbeidsforhold.getArbeidssted()) {
            case Person person -> person.getIdent(FOLKEREGISTERIDENT);
            case Underenhet underenhet -> underenhet.getIdent();
            default -> throw new IllegalStateException("Unexpected value: " + arbeidsforhold.getArbeidssted());
        };

        var arbeidstakerId = arbeidsforhold.getArbeidstaker().getIdent(FOLKEREGISTERIDENT);

        return new TilgangsforespoerselRequest.Arbeidstaker(opplysningspliktigId, arbeidsstedId, arbeidstakerId);
    }
}
