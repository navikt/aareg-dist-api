package no.nav.aareg.dist.api.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AaregAuthenticationResolver aaregAuthenticationResolver;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        var pprm = PathPatternRequestMatcher.withDefaults();
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(oauth -> oauth.authenticationManagerResolver(aaregAuthenticationResolver))
                .authorizeHttpRequests(
                        request -> request
                                .requestMatchers(
                                        pprm.matcher("/internal/**"),
                                        pprm.matcher("/actuator/**"),
                                        pprm.matcher("/error/**"),
                                        pprm.matcher("/status")
                                ).permitAll()
                                .requestMatchers(
                                        pprm.matcher("/graphql")
                                ).fullyAuthenticated()
                                .anyRequest().denyAll()
                ).build();
    }
}
