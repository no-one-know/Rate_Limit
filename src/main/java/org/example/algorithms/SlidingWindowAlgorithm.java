package org.example.algorithms;

import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.example.RateLimitResult;
import org.example.algorithms.scripts.SlidingWindowLuaScript;
import org.springframework.stereotype.Component;
import java.util.List;

@Component("sliding-window")
public class SlidingWindowAlgorithm implements RateLimitAlgorithm {

    private final StatefulRedisConnection<String, String> connection;
    private final String scriptSha;

    public SlidingWindowAlgorithm(StatefulRedisConnection<String, String> connection) {
        this.connection = connection;
        String luaScript = SlidingWindowLuaScript.SCRIPT;
        RedisCommands<String, String> commands = connection.sync();
        this.scriptSha = commands.scriptLoad(luaScript);
    }

    @Override
    public RateLimitResult execute(String redisKey, int capacity, int windowSeconds, long timestampSeconds) {
        RedisCommands<String, String> commands = connection.sync();
        List<Long> result = commands.evalsha(
                scriptSha,
                ScriptOutputType.MULTI,
                new String[]{redisKey},
                String.valueOf(capacity),
                String.valueOf(windowSeconds),
                String.valueOf(timestampSeconds)
        );
        boolean allowed = result.get(0) == 1;
        long remaining = result.get(1);
        return new RateLimitResult(allowed, remaining);
    }
}
