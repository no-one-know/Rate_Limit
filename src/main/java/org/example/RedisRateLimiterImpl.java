package org.example;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.List;

public class RedisRateLimiterImpl implements RedisRateLimiter {

    private static final int DEFAULT_TTL_SECONDS = 3600; // 1 hour

    private final StatefulRedisConnection<String, String> connection;
    private final String scriptSha;

    public RedisRateLimiterImpl(StatefulRedisConnection<String, String> connection) {
        this.connection = connection;
        String luaScript = TokenBucketLuaScript.SCRIPT;

        RedisCommands<String, String> commands = connection.sync();
        this.scriptSha = commands.scriptLoad(luaScript);
    }

    @Override
    public RateLimitResult execute(
            String redisKey,
            RateLimitConfig config,
            long timestampSeconds
    ) {
        RedisCommands<String, String> commands = connection.sync();

        List<Long> result = commands.evalsha(
                scriptSha,
                io.lettuce.core.ScriptOutputType.MULTI,
                new String[]{redisKey},
                String.valueOf(config.getCapacity()),
                String.valueOf(config.getRefillRate()),
                String.valueOf(timestampSeconds),
                "1",
                String.valueOf(DEFAULT_TTL_SECONDS)
        );

        boolean allowed = result.get(0) == 1;
        long remainingTokens = result.get(1);

        return new RateLimitResult(allowed, remainingTokens);
    }
}
