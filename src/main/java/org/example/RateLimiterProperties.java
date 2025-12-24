package org.example;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.Map;

@ConfigurationProperties(prefix = "rate-limiter")
public class RateLimiterProperties {

    private boolean enabled;
    private FailureMode failureMode;
    private Map<String, StrategyProperties> strategies;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public FailureMode getFailureMode() {
        return failureMode;
    }

    public void setFailureMode(FailureMode failureMode) {
        this.failureMode = failureMode;
    }

    public Map<String, StrategyProperties> getStrategies() {
        return strategies;
    }

    public void setStrategies(Map<String, StrategyProperties> strategies) {
        this.strategies = strategies;
    }
}
