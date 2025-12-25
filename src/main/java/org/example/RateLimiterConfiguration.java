package org.example;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.List;

@Configuration
@EnableConfigurationProperties({
        RateLimiterProperties.class,
        RedisConfigurationProperties.class,
        RedisPoolProperties.class
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
            RateLimiterProperties properties,
            RedisRateLimiter redisRateLimiter
    ) {

        List<RateLimitKeyStrategy> strategies =
                RateLimitStrategyFactory.createStrategies(
                        properties.getStrategies()
                );

        return new RateLimiterService(
                strategies,
                redisRateLimiter,
                properties.getFailureMode()
        );
    }
}
