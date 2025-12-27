package org.example;

import org.example.properties.entities.FailureMode;

public class RateLimiterService implements RateLimiter {

    private final RedisRateLimiter redisRateLimiter;
    private final FailureMode failureMode;

    public RateLimiterService(RedisRateLimiter redisRateLimiter, FailureMode failureMode) {
        this.redisRateLimiter = redisRateLimiter;
        this.failureMode = failureMode;
    }

    @Override
    public RateLimitResult checkRateLimit(String redisKey, int capacity, int refillRate) {

        long nowSeconds = System.currentTimeMillis() / 1000;
        RateLimitResult result;
        try {
            result = redisRateLimiter.execute(
                            redisKey,
                    capacity,
                    refillRate,
                            nowSeconds
                    );
        } catch (Exception ex) {

            if (failureMode == FailureMode.FAIL_OPEN) {
                // Allow request if Redis is unavailable
                return RateLimitResult.ALLOWED;
            }
            throw ex;
        }
        return result;
    }
}
