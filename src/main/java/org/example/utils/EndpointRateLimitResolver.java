package org.example.utils;

import org.example.properties.entities.EndpointRateLimitPolicy;
import org.example.properties.entities.SecondaryRateLimit;
import org.example.properties.RateLimiterProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class EndpointRateLimitResolver {

    private final Map<EndpointKey, EndpointRateLimitPolicy> policies;

    public EndpointRateLimitResolver(RateLimiterProperties props) {
        this.policies = new HashMap<>();

        for (EndpointRateLimitPolicy policy : props.getEndpoints()) {

            validate(policy);

            EndpointKey key =
                    new EndpointKey(policy.getMethod(), policy.getPath());

            if (policies.containsKey(key)) {
                throw new IllegalStateException(
                        "Duplicate rate-limit policy for " +
                                policy.getMethod() + " " + policy.getPath()
                );
            }

            policies.put(key, policy);
        }
    }

    public Optional<EndpointRateLimitPolicy> resolve(
            HttpMethod method,
            String path
    ) {
        return Optional.ofNullable(
                policies.get(new EndpointKey(method, path))
        );
    }

    private void validate(EndpointRateLimitPolicy policy) {

        if (policy.getPrimary() == null) {
            throw new IllegalStateException(
                    "Primary rate limit missing for " +
                            policy.getMethod() + " " + policy.getPath()
            );
        }

        if (policy.getSecondary() != null) {
            SecondaryRateLimit sec = policy.getSecondary();
            if (sec.getSource() == null || sec.getKey() == null) {
                throw new IllegalStateException(
                        "Secondary rate limit invalid for " +
                                policy.getMethod() + " " + policy.getPath()
                );
            }
        }
    }
}
