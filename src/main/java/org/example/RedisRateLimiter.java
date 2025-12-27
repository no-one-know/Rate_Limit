package org.example;

public interface RedisRateLimiter {
    RateLimitResult execute(String redisKey, int capacity, int refillRate, long timestampSeconds);
}
