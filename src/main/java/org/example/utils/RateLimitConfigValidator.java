package org.example.utils;

import org.example.RateLimitExecutionConfig;
import org.example.ResolvedEndpointRateLimit;
import org.example.algorithms.RateLimitAlgorithmType;
import org.example.properties.entities.EndpointRateLimitPolicy;
import org.example.properties.entities.PrimaryRateLimit;
import org.example.properties.entities.SecondaryRateLimit;

public final class RateLimitConfigValidator {

    public static ResolvedEndpointRateLimit validateAndResolve(
            RateLimitAlgorithmType algorithm,
            EndpointRateLimitPolicy policy
    ) {

        // ---------- PRIMARY ----------
        PrimaryRateLimit primary = policy.getPrimary();
        if (primary == null) {
            throw new IllegalStateException(
                    "Primary rate limit missing for " +
                            policy.getMethod() + " " + policy.getPath()
            );
        }

        RateLimitExecutionConfig primaryExec =
                validateLimitBlock(
                        algorithm,
                        primary.getCapacity(),
                        primary.getRefillRate(),
                        primary.getLeakRate(),
                        primary.getTimeWindow(),
                        "primary",
                        policy
                );

        // ---------- SECONDARY (OPTIONAL) ----------
        RateLimitExecutionConfig secondaryExec = null;
        SecondaryRateLimit secondaryMeta = null;

        if (policy.getSecondary() != null) {
            SecondaryRateLimit sec = policy.getSecondary();

            if (sec.getSource() == null || sec.getKey() == null) {
                throw new IllegalStateException(
                        "Secondary rate limit invalid for " +
                                policy.getMethod() + " " + policy.getPath()
                );
            }

            secondaryExec =
                    validateLimitBlock(
                            algorithm,
                            sec.getCapacity(),
                            sec.getRefillRate(),
                            sec.getLeakRate(),
                            sec.getTimeWindow(),
                            "secondary",
                            policy
                    );

            secondaryMeta = sec;
        }

        return new ResolvedEndpointRateLimit(
                primaryExec,
                secondaryExec,
                secondaryMeta
        );
    }

    // ---------- SHARED VALIDATION ----------
    private static RateLimitExecutionConfig validateLimitBlock(
            RateLimitAlgorithmType algorithm,
            int capacity,
            Integer refill,
            Integer leak,
            Integer window,
            String blockName,
            EndpointRateLimitPolicy policy
    ) {
        if (capacity <= 0) {
            throw new IllegalStateException(
                    "Capacity must be > 0 for " + blockName +
                            " limit on " + policy.getMethod() + " " + policy.getPath()
            );
        }

        switch (algorithm) {
            case TOKEN_BUCKET -> {
                if (refill == null || refill <= 0) {
                    throw new IllegalStateException(
                            "refill-rate must be provided and > 0 for " +
                                    blockName + " limit on " +
                                    policy.getMethod() + " " + policy.getPath()
                    );
                }
                if (leak != null || window != null) {
                    throw new IllegalStateException(
                            "Only refill-rate is allowed for token-bucket on " +
                                    blockName + " limit of " +
                                    policy.getMethod() + " " + policy.getPath()
                    );
                }
                return new RateLimitExecutionConfig(capacity, refill);
            }
            case LEAKING_BUCKET -> {
                if (leak == null || leak <= 0) {
                    throw new IllegalStateException(
                            "leak-rate must be provided and > 0 for " +
                                    blockName + " limit on " +
                                    policy.getMethod() + " " + policy.getPath()
                    );
                }
                if (refill != null || window != null) {
                    throw new IllegalStateException(
                            "Only leak-rate is allowed for leaking-bucket on " +
                                    blockName + " limit of " +
                                    policy.getMethod() + " " + policy.getPath()
                    );
                }
                return new RateLimitExecutionConfig(capacity, leak);
            }

            case FIXED_WINDOW, SLIDING_WINDOW -> {
                if (window == null || window <= 0) {
                    throw new IllegalStateException(
                            "time-window must be provided and > 0 for " +
                                    blockName + " limit on " +
                                    policy.getMethod() + " " + policy.getPath()
                    );
                }
                if (refill != null || leak != null) {
                    throw new IllegalStateException(
                            "Only time-window is allowed for " +
                                    algorithm.name().toLowerCase().replace('_', '-') +
                                    " on " + blockName + " limit of " +
                                    policy.getMethod() + " " + policy.getPath()
                    );
                }
                return new RateLimitExecutionConfig(capacity, window);
            }
            default -> throw new IllegalStateException(
                    "Unsupported rate limit algorithm: " + algorithm
            );
        }
    }

    private RateLimitConfigValidator() {}
}
