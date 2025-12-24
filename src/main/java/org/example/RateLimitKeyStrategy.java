package org.example;

public interface RateLimitKeyStrategy {
    String getName();
    boolean isEnabled();
    RateLimitConfig getConfig();
    String resolveKey(RequestContext context);
}
