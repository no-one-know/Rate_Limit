package org.example;

public interface RateLimiter {
    public RateLimitResult allow(RequestContext requestContext);
}
