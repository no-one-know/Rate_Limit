package org.example;

import org.example.algorithms.RateLimitAlgorithm;
import org.example.algorithms.RateLimitAlgorithmResolver;
import org.example.properties.RateLimiterProperties;
import org.example.properties.entities.FailureMode;
import org.springframework.stereotype.Component;

@Component
public class RateLimiterService implements RateLimiter {

    private final RateLimitAlgorithm rateLimitAlgorithm;
    private final FailureMode failureMode;

    public RateLimiterService(RateLimitAlgorithmResolver rateLimitAlgorithmResolver, RateLimiterProperties rateLimiterProperties) {
        this.rateLimitAlgorithm = rateLimitAlgorithmResolver.resolve();
        this.failureMode = rateLimiterProperties.getFailureMode();
    }

    @Override
    public RateLimitResult checkRateLimit(String redisKey, int capacity, int refillRate) {

        long nowSeconds = System.currentTimeMillis() / 1000;
        RateLimitResult result;
        try {
            result = rateLimitAlgorithm.execute(
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
