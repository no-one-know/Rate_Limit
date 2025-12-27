package org.example.properties.entities;

import org.springframework.http.HttpMethod;

public class EndpointRateLimitPolicy {

    private HttpMethod method;
    private String path;

    private PrimaryRateLimit primary;
    private SecondaryRateLimit secondary;

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public PrimaryRateLimit getPrimary() {
        return primary;
    }

    public SecondaryRateLimit getSecondary() {
        return secondary;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setPrimary(PrimaryRateLimit primary) {
        this.primary = primary;
    }

    public void setSecondary(SecondaryRateLimit secondary) {
        this.secondary = secondary;
    }
}
