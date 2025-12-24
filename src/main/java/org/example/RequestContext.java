package org.example;

public class RequestContext {

    private final String ip;
    private final String apiPath;
    private final String apiKey;
    private final String userId;
    private final long timestamp;

    public RequestContext(
            String ip,
            String apiPath,
            String apiKey,
            String userId,
            long timestamp
    ) {
        this.ip = ip;
        this.apiPath = apiPath;
        this.apiKey = apiKey;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public String getIp() {
        return ip;
    }

    public String getApiPath() {
        return apiPath;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getUserId() {
        return userId;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
