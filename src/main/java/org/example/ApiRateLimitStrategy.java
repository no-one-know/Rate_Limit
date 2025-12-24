package org.example;

public class ApiRateLimitStrategy implements RateLimitKeyStrategy {

    private final RateLimitConfig config;
    private final boolean enabled;

    public ApiRateLimitStrategy(RateLimitConfig config, boolean enabled) {
        this.config = config;
        this.enabled = enabled;
    }

    @Override
    public String getName() {
        return "api";
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public RateLimitConfig getConfig() {
        return config;
    }

    @Override
    public String resolveKey(RequestContext context) {
        return "rl:api:" + context.getApiPath();
    }
}
