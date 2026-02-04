package no.nav.aareg.dist.api.audit;

import no.nav.aareg.dist.api.consumer.aareg.tilgangskontroll.dto.Tilgang;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.time.Instant.now;
import static org.slf4j.event.Level.INFO;
import static org.slf4j.event.Level.WARN;
import static org.springframework.util.StringUtils.hasText;

@Component
public class AuditLogger {

    private static final Logger AUDIT_LOGGER = LoggerFactory.getLogger("auditLogger");

    public void loggTilgang(Auditelement auditelement, List<Tilgang> tilganger) {
        var epochInMillis = now().toEpochMilli();
        auditelement.getArbeidstakeridentifikatorer().forEach(arbeidstakerId -> {
            var harTilgang = harTilgang(arbeidstakerId, tilganger);
            var harAdressebeskyttelse = harAdressebeskyttelse(arbeidstakerId, tilganger);
            var auditmelding = lagAuditmelding(
                    arbeidstakerId,
                    auditelement,
                    harTilgang,
                    harAdressebeskyttelse ? WARN : harTilgang ? INFO : WARN,
                    epochInMillis
            );

            AUDIT_LOGGER.info(auditmelding);
        });
    }

    public void loggIngenTilgang(Auditelement auditelement) {
        var epochInMillis = now().toEpochMilli();
        auditelement.getArbeidstakeridentifikatorer().forEach(arbeidstakerId -> {
            var auditmelding = lagAuditmelding(
                    arbeidstakerId,
                    auditelement,
                    WARN,
                    epochInMillis
            );

            AUDIT_LOGGER.info(auditmelding);
        });
    }

    private String lagAuditmelding(
            String arbeidstakerId,
            Auditelement auditelement,
            boolean harTilgang,
            Level loggnivaa,
            long epochInMillis
    ) {
        // CEF-format => CEF:0|Aareg|<kilde:applikasjonsnavn>|<versjon:1.0>|audit:access|<ressurs>|<loggnivå:INFO|WARN>|flexString1Label=Decision flexString1=<permit|deny> dproc=<konsument-id> suid=<bruker-id> sproc=<call-id> end=<epoch-millis> duid=<arbeidstaker-id>> cs3Label=Dataforespoersel cs3=<dataforespoersel> cn3Label=Dataresponsantall cn3=<antall arbeidsforhold>

        return "CEF:0" + "|"
                + "Aareg" + "|"
                + auditelement.getKilde() + "|"
                + "1.0" + "|"
                + "audit:access" + "|"
                + auditelement.getRessurs() + "|"
                + loggnivaa.name() + "|"
                + "flexString1Label" + "=" + "Decision" + " "
                + "flexString1" + "=" + (harTilgang ? "permit" : "deny") + " "
                + "dproc" + "=" + (auditelement.getKonsumentId() != null ? auditelement.getKonsumentId() : auditelement.getKorrelasjonId()) + " "
                + "suid" + "=" + finnRequester(auditelement) + " "
                + "sproc" + "=" + auditelement.getKorrelasjonId() + " "
                + "end" + "=" + epochInMillis + " "
                + "duid" + "=" + arbeidstakerId + " "
                + "cs3Label" + "=" + "Dataforespoersel" + " "
                + "cs3" + "=" + auditelement.getDataforespoersel() + ";" + auditelement.getDatafilter() + " "
                + "cn1Label" + "=" + "Dataresponsantall" + " "
                + "cn1" + "=" + auditelement.getDataresponsAntall();
    }

    private String lagAuditmelding(
            String arbeidstakerId,
            Auditelement auditelement,
            Level loggnivaa,
            long epochInMillis
    ) {
        // CEF-format => CEF:0|Aareg|<kilde:applikasjonsnavn>|<versjon:1.0>|audit:access|<ressurs>|<loggnivå:INFO|WARN>|flexString1Label=Decision flexString1=<permit|deny> dproc=<konsument-id> suid=<bruker-id> sproc=<call-id> end=<epoch-millis> duid=<arbeidstaker-id>> cs3Label=Dataforespoersel cs3=<dataforespoersel> cn3Label=Dataresponsantall cn3=<antall arbeidsforhold>

        return "CEF:0" + "|"
                + "Aareg" + "|"
                + auditelement.getKilde() + "|"
                + "1.0" + "|"
                + "audit:access" + "|"
                + auditelement.getRessurs() + "|"
                + loggnivaa.name() + "|"
                + "flexString1Label" + "=" + "Decision" + " "
                + "flexString1" + "=" + (loggnivaa == WARN ? "deny" : "permit") + " "
                + "dproc" + "=" + auditelement.getKonsumentId() + " "
                + "suid" + "=" + finnRequester(auditelement) + " "
                + "sproc" + "=" + auditelement.getKorrelasjonId() + " "
                + "end" + "=" + epochInMillis + " "
                + "duid" + "=" + arbeidstakerId + " "
                + "cs3Label" + "=" + "Dataforespoersel" + " "
                + "cs3" + "=" + auditelement.getDataforespoersel() + ";" + auditelement.getDatafilter() + " "
                + "cn1Label" + "=" + "Dataresponsantall" + " "
                + "cn1" + "=" + (loggnivaa == INFO ? auditelement.getDataresponsAntall() : 0);
    }

    private boolean harTilgang(String arbeidstakerId, List<Tilgang> tilganger) {
        var targetTilgang = getTargetTilgang(arbeidstakerId, tilganger);
        if (targetTilgang == null) {
            return false;
        } else {
            return targetTilgang.harTilgang();
        }
    }

    private boolean harAdressebeskyttelse(String arbeidstakerId, List<Tilgang> tilganger) {
        var targetTilgang = getTargetTilgang(arbeidstakerId, tilganger);
        if (targetTilgang == null) {
            return false;
        } else {
            return targetTilgang.adressebeskyttetArbeidstaker() || targetTilgang.adressebeskyttetOpplysningspliktig();
        }
    }

    private static @Nullable Tilgang getTargetTilgang(String arbeidstakerId, List<Tilgang> tilganger) {
        return tilganger.stream().filter(t -> t.arbeidstakerIdentifkator().equals(arbeidstakerId)).findFirst().orElse(null);
    }

    private String finnRequester(Auditelement auditelement) {
        if (hasText(auditelement.getBrukerId())) {
            return auditelement.getBrukerId();
        } else if (hasText(auditelement.getDatabehandlerId())) {
            return auditelement.getDatabehandlerId();
        }
        return auditelement.getKonsumentId();
    }
}
