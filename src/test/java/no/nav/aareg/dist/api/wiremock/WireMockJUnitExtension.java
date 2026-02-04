package no.nav.aareg.dist.api.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * Kopiert fra hdir-fkr-proxy
 *
 * <p>This JUnit 5 Extension exists so we can set up a WireMock server before the Spring context starts in the tests.
 * Because of this, it's very important that this annotation is defined before {@code @ExtendWith(SpringRunner.class)},
 * because the order of {@link org.junit.jupiter.api.extension.ExtendWith @ExtendWith} declarations matters. This
 * annotation also resets the WireMock server before each test and runs
 * {@link WireMockStub#registerResponseMappingBeforeEach()} on each registered stub.</p>
 *
 * <p>It's for clarity, it's recommended to not use this class directly, but rather through the
 * {@link WireMockStubs}-annotation, which also enables the user to define a list of {@link WireMockStub} classes to
 * register.</p>
 */
public class WireMockJUnitExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback {

    private Map<Class<? extends WireMockStub>, WireMockStub> stubs;

    @Override
    public void beforeAll(ExtensionContext context) {
        stubs = new LinkedHashMap<>();

        WireMockConfiguration options = wireMockConfig()
                .dynamicPort()
                .notifier(new Slf4jNotifier(true));

        Class<? extends WireMockStub>[] stubClasses = findExtensionsFromAnnotation(context);
        Stream.of(stubClasses)
                .map(this::createInstanceOf)
                .peek(stubInstance -> stubs.put(stubInstance.getClass(), stubInstance))
                .filter(stubInstance -> stubInstance.getWireMockExtension() != null)
                .forEach(stubInstance -> options.extensions(stubInstance.getWireMockExtension()));

        WireMockServer server = new WireMockServer(options);
        server.start();
        WireMock.configureFor(new WireMock(server));
        System.setProperty("wiremock.server.port", String.valueOf(server.port()));

        stubs.values().forEach(WireMockStub::registerResponseMappingBeforeAll);
    }

    private WireMockStub createInstanceOf(Class<? extends WireMockStub> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to instantiate WireMock extension", exception);
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        WireMock.reset();
        stubs.values().forEach(WireMockStub::registerResponseMappingBeforeEach);
        context.getTestInstance().ifPresent(testInstance -> assignStubsTo(testInstance, testInstance.getClass()));
    }

    @Override
    public void afterAll(ExtensionContext context) {
        // Ikke utfør 'server.stop();' her
    }

    private Class<? extends WireMockStub>[] findExtensionsFromAnnotation(ExtensionContext context) {
        if (context.getTestClass().isPresent()) {
            WireMockStubs annotation = context.getTestClass().get().getAnnotation(WireMockStubs.class);
            if (annotation != null) {
                return annotation.value();
            }
        }

        return new Class[0];
    }

    private void assignStubsTo(Object testInstance, Class<?> type) {
        Stream.of(type.getDeclaredFields())
                .filter(field -> stubs.containsKey(field.getType()))
                .forEach(field -> {
                    try {
                        boolean previouslyAccessible = field.canAccess(testInstance);
                        field.setAccessible(true);
                        field.set(testInstance, stubs.get(field.getType()));
                        field.setAccessible(previouslyAccessible);
                    } catch (Exception exception) {
                        throw new RuntimeException("Failed to assign stub to test instance", exception);
                    }
                });

        if (type.getDeclaredAnnotation(WireMockStubs.class) == null) {
            assignStubsTo(testInstance, type.getSuperclass());
        }
    }
}
