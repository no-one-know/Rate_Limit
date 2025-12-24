package org.example;

public interface RedisRateLimiter {
    RateLimitResult execute(String redisKey, RateLimitConfig config, long timestampSeconds);
}
