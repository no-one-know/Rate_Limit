package org.example.algorithms;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.example.RateLimitResult;
import org.springframework.stereotype.Component;

@Component("fixed-window")
public class FixedWindowAlgorithm implements RateLimitAlgorithm {

    private final StatefulRedisConnection<String, String> connection;

    public FixedWindowAlgorithm(StatefulRedisConnection<String, String> connection) {
        this.connection = connection;
    }

    @Override
    public RateLimitResult execute(
            String redisKey,
            int capacity,
            int windowSeconds,
            long timestampSeconds
    ) {
        RedisCommands<String, String> commands = connection.sync();

        Long count = commands.incr(redisKey);

        if (count == 1) {
            commands.expire(redisKey, windowSeconds);
        }

        boolean allowed = count <= capacity;
        long remaining = Math.max(0, capacity - count);

        return new RateLimitResult(allowed, remaining);
    }
}
