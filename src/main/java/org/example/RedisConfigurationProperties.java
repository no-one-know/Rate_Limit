package org.example;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "redis")
public class RedisConfigurationProperties {
    private String host;
    private int port;
    private Duration timeout;

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public Duration getTimeout() {
        return this.timeout;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }
}
