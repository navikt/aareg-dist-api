package no.nav.aareg.dist.api.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import lombok.extern.slf4j.Slf4j;
import org.mockito.ArgumentCaptor;
import org.slf4j.LoggerFactory;

import static ch.qos.logback.classic.Level.TRACE;
import static no.nav.aareg.dist.api.graphql.utils.AaregCollectors.toSingleton;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
public class LoggerMockingUtil {

    private static final String SYSTEM_LOGGER = "no.nav.aareg";
    private static final String AUDIT_LOGGER = "auditLogger";
    private static final String TEAM_LOGGER = "team-logger";

    public static Appender<ILoggingEvent> getMockedSystemLogAppender() {
        return getMockedAppender(SYSTEM_LOGGER, TRACE);
    }

    public static Appender<ILoggingEvent> getMockedAuditLogAppender() {
        return getMockedAppender(AUDIT_LOGGER, TRACE);
    }

    public static Appender<ILoggingEvent> getMockedAppender(String loggerName, Level loggingLevel) {
        Logger logger = (Logger) LoggerFactory.getLogger(loggerName);
        return getMockedAppender(logger, loggingLevel);
    }

    /*
     * MERK: Forutsetter at det finnes bare 1 loggmelding for angitt loggnivå (level) og korrelasjon-id
     */
    public static String getApiLogMessage(ArgumentCaptor<ILoggingEvent> logEventArgumentCaptor, Level level, String korrelasjonId) {
        var values = logEventArgumentCaptor.getAllValues();
        return values.stream()
                .filter(loggingEvent -> loggingEvent.getLevel().equals(level))
                .map(ILoggingEvent::getFormattedMessage)
                .filter(message -> message.contains(korrelasjonId))
                .collect(toSingleton());
    }

    public static boolean logMessageExists(ArgumentCaptor<ILoggingEvent> logEventArgumentCaptor, Level loggingLevel, String korrelasjonId, String message) {
        var values = logEventArgumentCaptor.getAllValues();
        return values.stream()
                .filter(loggingEvent -> loggingEvent.getLevel().equals(loggingLevel))
                .map(ILoggingEvent::getFormattedMessage)
                .filter(logMessage -> logMessage.contains(korrelasjonId))
                .anyMatch(logMessage -> logMessage.contains(message));
    }

    @SuppressWarnings("unchecked")
    private static Appender<ILoggingEvent> getMockedAppender(Logger logger, Level loggingLevel) {
        logger.setLevel(loggingLevel);
        Appender<ILoggingEvent> mockAppender = mock(Appender.class);
        when(mockAppender.getName()).thenReturn("MOCK");
        logger.addAppender(mockAppender);
        return mockAppender;
    }
}
