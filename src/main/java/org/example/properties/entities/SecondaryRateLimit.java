package org.example.properties.entities;

public class SecondaryRateLimit {

    private IdentifierSource source;
    private String key;
    private int capacity;
    private int refillRate;

    public IdentifierSource getSource() {
        return source;
    }

    public String getKey() {
        return key;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getRefillRate() {
        return refillRate;
    }

    public void setSource(IdentifierSource source) {
        this.source = source;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setRefillRate(int refillRate) {
        this.refillRate = refillRate;
    }
}
