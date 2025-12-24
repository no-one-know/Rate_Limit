package org.example;

public class ApiKeyRateLimitStrategy implements RateLimitKeyStrategy {

    private final RateLimitConfig config;
    private final boolean enabled;

    public ApiKeyRateLimitStrategy(RateLimitConfig config, boolean enabled) {
        this.config = config;
        this.enabled = enabled;
    }

    @Override
    public String getName() {
        return "api-key";
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
        return "rl:apikey:" + context.getApiKey();
    }
}
