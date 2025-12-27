package org.example.interceptors;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RequestLoggingInterceptor implements Filter {

    private static final Logger log =
            LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws java.io.IOException, jakarta.servlet.ServletException {

        HttpServletRequest httpRequest =
                (HttpServletRequest) request;
        HttpServletResponse httpResponse =
                (HttpServletResponse) response;

        Instant startTime = Instant.now();

        log.info(
                "Request started | method={} uri={} timestamp={}",
                httpRequest.getMethod(),
                httpRequest.getRequestURI(),
                startTime
        );

        try {
            chain.doFilter(request, response);
        } finally {
            Instant endTime = Instant.now();

            log.info(
                    "Request completed | method={} uri={} status={} timestamp={}",
                    httpRequest.getMethod(),
                    httpRequest.getRequestURI(),
                    httpResponse.getStatus(),
                    endTime
            );
        }
    }
}
