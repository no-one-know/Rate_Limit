package org.example;

public class RateLimitResult {

    private final boolean allowed;
    private final long remainingTokens;

    public static final RateLimitResult ALLOWED = new RateLimitResult(true, -1);

    public RateLimitResult(boolean allowed, long remainingTokens) {
        this.allowed = allowed;
        this.remainingTokens = remainingTokens;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public long getRemainingTokens() {
        return remainingTokens;
    }
}
