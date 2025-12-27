package org.example.properties;

import org.example.properties.entities.EndpointRateLimitPolicy;
import org.example.properties.entities.FailureMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "rate-limiter")
public class RateLimiterProperties {

    private boolean enabled;
    private FailureMode failureMode;
    private List<EndpointRateLimitPolicy> endpoints = new ArrayList<>();

    public boolean isEnabled() {
        return enabled;
    }

    public FailureMode getFailureMode() {
        return failureMode;
    }

    public List<EndpointRateLimitPolicy> getEndpoints() {
        return endpoints;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setFailureMode(FailureMode failureMode) {
        this.failureMode = failureMode;
    }

    public void setEndpoints(List<EndpointRateLimitPolicy> endpoints) {
        this.endpoints = endpoints;
    }
}
