package org.example;

public class RateLimitConfig {

    private final int capacity;
    private final int refillRate;

    public RateLimitConfig(int capacity, int refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getRefillRate() {
        return refillRate;
    }
}
