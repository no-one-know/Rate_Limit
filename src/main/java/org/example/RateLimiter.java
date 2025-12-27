package org.example;

public interface RateLimiter {
    public RateLimitResult checkRateLimit(String redisKey, int capacity, int refillRate);
}
