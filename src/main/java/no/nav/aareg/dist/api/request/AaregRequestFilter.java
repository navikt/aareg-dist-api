package no.nav.aareg.dist.api.request;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static java.util.UUID.randomUUID;
import static no.nav.aareg.dist.api.request.RequestAttributes.ATTRIBUTE_AAREG_SERVER_NAVN;
import static no.nav.aareg.dist.api.request.RequestHeaders.CORRELATION_ID;
import static org.springframework.util.StringUtils.hasText;

@Order(-200) // Spring security har order=-100
@Component
public class AaregRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            var correlationId = httpServletRequest.getHeader(CORRELATION_ID);

            if (!hasText(correlationId)) {
                correlationId = randomUUID().toString();
            }

            MDC.put(CORRELATION_ID, correlationId);
            httpServletRequest.setAttribute(CORRELATION_ID, correlationId);
            httpServletResponse.setHeader(CORRELATION_ID, correlationId);

            httpServletRequest.setAttribute(ATTRIBUTE_AAREG_SERVER_NAVN, httpServletRequest.getRequestURL());

            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } finally {
            MDC.remove(CORRELATION_ID);
        }
    }
}
