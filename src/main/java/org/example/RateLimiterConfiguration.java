package org.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;

@Configuration
@EnableConfigurationProperties(RateLimiterProperties.class)
public class RateLimiterConfiguration {

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(8);
        poolConfig.setMaxIdle(8);
        poolConfig.setMinIdle(0);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);

        return new JedisPool(poolConfig, "localhost", 6379);
    }

    @Bean
    public RedisRateLimiter redisRateLimiter(JedisPool jedisPool) {
        return new RedisRateLimiterImpl(jedisPool);
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
