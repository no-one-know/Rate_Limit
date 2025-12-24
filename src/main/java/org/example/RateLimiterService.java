package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class RateLimiterService implements RateLimiter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimiterService.class);

    private final List<RateLimitKeyStrategy> strategies;
    private final RedisRateLimiter redisRateLimiter;
    private final FailureMode failureMode;

    public RateLimiterService(List<RateLimitKeyStrategy> strategies, RedisRateLimiter redisRateLimiter, FailureMode failureMode) {
        this.strategies = strategies;
        this.redisRateLimiter = redisRateLimiter;
        this.failureMode = failureMode;
    }

    @Override
    public RateLimitResult allow(RequestContext context) {

        long nowSeconds = context.getTimestamp();

        for (RateLimitKeyStrategy strategy : strategies) {

            if (!strategy.isEnabled()) {
                continue;
            }

            String redisKey = strategy.resolveKey(context);

            try {
                RateLimitResult result =
                        redisRateLimiter.execute(
                                redisKey,
                                strategy.getConfig(),
                                nowSeconds
                        );

                if (!result.isAllowed()) {
                    return result;
                }

            } catch (Exception ex) {

                if (failureMode == FailureMode.FAIL_OPEN) {
                    // Allow request if Redis is unavailable
                    logger.error("Redis rate limiter failed for strategy: {}. Failing open. Error: {}",
                            strategy.getName(), ex.getMessage(), ex);
                    continue;
                }

                logger.error("Redis rate limiter failed for strategy: {}. Failing closed.",
                        strategy.getName(), ex);
                throw ex;
            }
        }

        return RateLimitResult.ALLOWED;
    }
}
