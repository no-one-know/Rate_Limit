package org.example;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import java.util.List;

public class RedisRateLimiterImpl implements RedisRateLimiter {

    private static final int DEFAULT_TTL_SECONDS = 3600; // 1 hour

    private final JedisPool jedisPool;
    private final String luaScript;
    private final String scriptSha;

    public RedisRateLimiterImpl(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        this.luaScript = TokenBucketLuaScript.SCRIPT;

        try (Jedis jedis = jedisPool.getResource()) {
            this.scriptSha = jedis.scriptLoad(luaScript);
        }
    }

    @Override
    public RateLimitResult execute(
            String redisKey,
            RateLimitConfig config,
            long timestampSeconds
    ) {

        try (Jedis jedis = jedisPool.getResource()) {

            @SuppressWarnings("unchecked")
            List<Long> result = (List<Long>) jedis.evalsha(
                    scriptSha,
                    List.of(redisKey),
                    List.of(
                            String.valueOf(config.getCapacity()),
                            String.valueOf(config.getRefillRate()),
                            String.valueOf(timestampSeconds),
                            "1",
                            String.valueOf(DEFAULT_TTL_SECONDS)
                    )
            );

            boolean allowed = result.get(0) == 1;
            long remainingTokens = result.get(1);

            return new RateLimitResult(allowed, remainingTokens);
        }
    }
}
