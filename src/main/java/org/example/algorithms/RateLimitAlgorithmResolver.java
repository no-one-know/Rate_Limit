package org.example.algorithms;

import org.example.properties.RateLimiterProperties;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class RateLimitAlgorithmResolver {

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
        RateLimitAlgorithm algorithm = algorithms.get(selectedAlgorithm);
        if (algorithm == null) {
            throw new IllegalStateException(
                    "Unknown rate limit algorithm: " + selectedAlgorithm
            );
        }
        return algorithm;
    }
}
