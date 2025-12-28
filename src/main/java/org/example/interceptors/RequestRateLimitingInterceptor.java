package org.example.interceptors;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.*;
import org.example.properties.entities.SecondaryRateLimit;
import org.example.properties.RateLimiterProperties;
import org.example.utils.EndpointRateLimitResolver;
import org.springframework.http.HttpMethod;
import java.io.IOException;
import java.util.Optional;

public class RequestRateLimitingInterceptor implements Filter {

    private final RateLimiter rateLimiter;
    private final RateLimiterProperties rateLimiterProperties;
    private final EndpointRateLimitResolver endpointRateLimitResolver;

    public RequestRateLimitingInterceptor(
            RateLimiter rateLimiter,
            RateLimiterProperties rateLimiterProperties,
            EndpointRateLimitResolver endpointRateLimitResolver
    ) {
        this.rateLimiter = rateLimiter;
        this.rateLimiterProperties = rateLimiterProperties;
        this.endpointRateLimitResolver = endpointRateLimitResolver;
    }

    @Override
    public void doFilter(
            ServletRequest req,
            ServletResponse res,
            FilterChain chain
    ) throws IOException, ServletException {

        if (!rateLimiterProperties.isEnabled()) {
            chain.doFilter(req, res);
            return;
        }

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        String path = request.getRequestURI();

        Optional<ResolvedEndpointRateLimit> optionalPolicy =
                endpointRateLimitResolver.resolve(method, path);

        // Endpoint not configured → no rate limiting
        if (optionalPolicy.isEmpty()) {
            chain.doFilter(req, res);
            return;
        }

        ResolvedEndpointRateLimit policy = optionalPolicy.get();

        // ---------- PRIMARY LIMIT ----------
        String primaryKey = String.format(
                "rl:primary:%s:%s",
                method.name(),
                path
        );

        RateLimitResult primaryResult =
                rateLimiter.checkRateLimit(
                        primaryKey,
                        policy.primary().capacity(),
                        policy.primary().rateOrWindow()
                );

        if (!primaryResult.isAllowed()) {
            block(response);
            return;
        }

        // ---------- SECONDARY LIMIT ----------
        if (policy.secondary() != null) {

            SecondaryRateLimit sec = policy.secondaryMeta();
            String identifier = extractIdentifier(request, sec);

            if (identifier == null || identifier.isBlank()) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Missing rate-limit identifier: " + sec.getKey()
                );
                return;
            }

            String secondaryKey = String.format(
                    "rl:secondary:%s:%s:%s",
                    method.name(),
                    path,
                    identifier
            );

            RateLimitResult secondaryResult =
                    rateLimiter.checkRateLimit(
                            secondaryKey,
                            policy.secondary().capacity(),
                            policy.secondary().rateOrWindow()
                    );

            if (!secondaryResult.isAllowed()) {
                block(response);
                return;
            }
        }

        chain.doFilter(req, res);
    }

    private void block(HttpServletResponse response) throws IOException {
        response.setStatus(429);
        response.getWriter().write("Rate limit exceeded");
    }

    private String extractIdentifier(
            HttpServletRequest request,
            SecondaryRateLimit sec
    ) {
        return switch (sec.getSource()) {
            case HEADER -> request.getHeader(sec.getKey());
            case QUERY_PARAM -> request.getParameter(sec.getKey());
        };
    }
}
