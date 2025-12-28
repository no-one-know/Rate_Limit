package org.example.properties.entities;

public class PrimaryRateLimit {

    private int capacity;
    private Integer refillRate;
    private Integer leakRate;
    private Integer timeWindow;

    public Integer getLeakRate() {
        return leakRate;
    }

    public void setLeakRate(int leakRate) {
        this.leakRate = leakRate;
    }

    public Integer getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(int timeWindow) {
        this.timeWindow = timeWindow;
    }

    public int getCapacity() {
        return capacity;
    }

    public Integer getRefillRate() {
        return refillRate;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setRefillRate(int refillRate) {
        this.refillRate = refillRate;
    }
}
