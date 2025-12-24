package org.example;

public class IpRateLimitStrategy implements RateLimitKeyStrategy {

    private final RateLimitConfig config;
    private final boolean enabled;

    public IpRateLimitStrategy(RateLimitConfig config, boolean enabled) {
        this.config = config;
        this.enabled = enabled;
    }

    @Override
    public String getName() {
        return "ip";
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
        return "rl:ip:" + context.getIp();
    }
}
