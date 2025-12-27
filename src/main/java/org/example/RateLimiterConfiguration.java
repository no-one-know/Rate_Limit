package org.example;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.example.properties.RateLimiterProperties;
import org.example.properties.RedisConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Configuration
@EnableConfigurationProperties({
        RateLimiterProperties.class,
        RedisConfigurationProperties.class
})
public class RateLimiterConfiguration {

    @Bean
    public RedisClient redisClient(RedisConfigurationProperties redisConfigurationProperties) {
        return RedisClient.create("redis://" + redisConfigurationProperties.getHost() + ":" + redisConfigurationProperties.getPort());
    }

    @Bean
    public StatefulRedisConnection<String, String> statefulRedisConnection(RedisClient redisClient) {
        return redisClient.connect();
    }

    @Bean
    public RedisRateLimiter redisRateLimiter(StatefulRedisConnection<String, String> connection) {
        return new RedisRateLimiterImpl(connection);
    }

    @Bean
    public RateLimiter rateLimiter(
            RateLimiterProperties rateLimiterProperties,
            RedisRateLimiter redisRateLimiter
    ) {
        return new RateLimiterService(
                redisRateLimiter,
                rateLimiterProperties.getFailureMode()
        );
    }
}
