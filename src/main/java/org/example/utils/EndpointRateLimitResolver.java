package org.example.utils;

import org.example.ResolvedEndpointRateLimit;
import org.example.algorithms.RateLimitAlgorithmType;
import org.example.properties.entities.EndpointRateLimitPolicy;
import org.example.properties.RateLimiterProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class EndpointRateLimitResolver {

    private final Map<EndpointKey, ResolvedEndpointRateLimit> policies;

    public EndpointRateLimitResolver(RateLimiterProperties props) {

        RateLimitAlgorithmType rateLimitAlgorithmType = RateLimitAlgorithmType.from(props.getAlgorithm());
        this.policies = new HashMap<>();

        for (EndpointRateLimitPolicy policy : props.getEndpoints()) {
            EndpointKey key = new EndpointKey(policy.getMethod(), policy.getPath());
            if (policies.containsKey(key)) {
                throw new IllegalStateException(
                        "Duplicate rate-limit policy for " +
                                policy.getMethod() + " " + policy.getPath()
                );
            }
            ResolvedEndpointRateLimit resolved = RateLimitConfigValidator.validateAndResolve(
                            rateLimitAlgorithmType,
                            policy
                    );
            policies.put(key, resolved);
        }
    }

    public Optional<ResolvedEndpointRateLimit> resolve(
            HttpMethod method,
            String path
    ) {
        return Optional.ofNullable(
                policies.get(new EndpointKey(method, path))
        );
    }
}
