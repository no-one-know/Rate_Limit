package org.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;

@Configuration
@EnableConfigurationProperties({
        RateLimiterProperties.class,
        RedisConfigurationProperties.class,
        RedisPoolProperties.class
})
public class RateLimiterConfiguration {

    @Bean
    public JedisPool jedisPool(RedisConfigurationProperties redisConfigurationProperties, RedisPoolProperties redisPoolProperties) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(redisPoolProperties.getMaxTotal());
        poolConfig.setMaxIdle(redisPoolProperties.getMaxIdle());
        poolConfig.setMinIdle(redisPoolProperties.getMinIdle());
        poolConfig.setMaxWait(redisConfigurationProperties.getTimeout());
        return new JedisPool(poolConfig, redisConfigurationProperties.getHost(), redisConfigurationProperties.getPort());
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
