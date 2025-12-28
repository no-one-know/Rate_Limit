package org.example.algorithms;

public enum RateLimitAlgorithmType {
    TOKEN_BUCKET("token-bucket"),
    LEAKING_BUCKET("leaking-bucket"),
    FIXED_WINDOW("fixed-window"),
    SLIDING_WINDOW("sliding-window");

    private final String key;

    RateLimitAlgorithmType(String key) {
        this.key = key;
    }

    public static RateLimitAlgorithmType from(String value) {
        for (RateLimitAlgorithmType t : values()) {
            if (t.key.equals(value)) {
                return t;
            }
        }
        // Default to TOKEN_BUCKET if no match is found
        return TOKEN_BUCKET;
    }
}
