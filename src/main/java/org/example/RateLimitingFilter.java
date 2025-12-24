package org.example;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RateLimitingFilter implements Filter {

    private final RateLimiter rateLimiter;

    public RateLimitingFilter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Build RequestContext from HTTP request
        String ip = extractClientIp(httpRequest);
        String apiPath = httpRequest.getRequestURI();
        String apiKey = httpRequest.getHeader("X-API-Key");
        String userId = null; // Can be extracted from JWT or other auth header
        long timestamp = System.currentTimeMillis() / 1000; // Convert to seconds

        RequestContext context = new RequestContext(ip, apiPath, apiKey, userId, timestamp);

        // Evaluate rate limiting
        RateLimitResult result = rateLimiter.allow(context);

        if (!result.isAllowed()) {
            // Return HTTP 429 Too Many Requests
            httpResponse.setStatus(429);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(
                    "{\"error\": \"Rate limit exceeded\", \"remaining_tokens\": " +
                    result.getRemainingTokens() + "}"
            );
            return;
        }

        // Allow request to proceed
        chain.doFilter(request, response);
    }

    /**
     * Extract client IP from request, considering proxy headers
     */
    private String extractClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            // X-Forwarded-For can contain multiple IPs; take the first one
            return forwardedFor.split(",")[0].trim();
        }

        String clientIp = request.getHeader("X-Client-IP");
        if (clientIp != null && !clientIp.isEmpty()) {
            return clientIp;
        }

        return request.getRemoteAddr();
    }
}
