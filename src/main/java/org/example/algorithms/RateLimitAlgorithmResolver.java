package org.example.algorithms;

import org.example.properties.RateLimiterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class RateLimitAlgorithmResolver {

    private final Logger logger = LoggerFactory.getLogger(RateLimitAlgorithmResolver.class);
    private static final String DEFAULT_ALGORITHM = "token-bucket";
    private final Map<String, RateLimitAlgorithm> algorithms;
    private final String selectedAlgorithm;

    public RateLimitAlgorithmResolver(
            Map<String, RateLimitAlgorithm> algorithms,
            RateLimiterProperties properties
    ) {
        this.algorithms = algorithms;
        this.selectedAlgorithm = properties.getAlgorithm();
    }

    public RateLimitAlgorithm resolve() {
        if (selectedAlgorithm != null && !selectedAlgorithm.isBlank()) {
            RateLimitAlgorithm configured = algorithms.get(selectedAlgorithm);
            if (configured != null) {
                return configured;
            }
        }
        RateLimitAlgorithm fallback = algorithms.get(DEFAULT_ALGORITHM);
        if (fallback == null) {
            throw new IllegalStateException(
                    "Default rate limit algorithm '" + DEFAULT_ALGORITHM + "' is not registered"
            );
        }
        logger.info("Using default rate limit algorithm '{}'", fallback);
        return fallback;
    }
}
