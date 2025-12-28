package org.example.algorithms.scripts;

public final class SlidingWindowLuaScript {

    public static final String SCRIPT = """
        local key = KEYS[1]
        local capacity = tonumber(ARGV[1])
        local window = tonumber(ARGV[2])
        local now = tonumber(ARGV[3])

        redis.call('ZREMRANGEBYSCORE', key, 0, now - window)

        local count = redis.call('ZCARD', key)

        if count < capacity then
            redis.call('ZADD', key, now, now)
            redis.call('EXPIRE', key, window)
            return {1, capacity - count - 1}
        else
            return {0, 0}
        end
        """;

    private SlidingWindowLuaScript() {}
}
