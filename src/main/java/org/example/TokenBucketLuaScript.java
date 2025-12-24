package org.example;

public final class TokenBucketLuaScript {

    public static final String SCRIPT = """
        local key = KEYS[1]

        local capacity = tonumber(ARGV[1])
        local refill_rate = tonumber(ARGV[2])
        local now = tonumber(ARGV[3])
        local requested = tonumber(ARGV[4])
        local ttl = tonumber(ARGV[5])

        local data = redis.call("HMGET", key, "tokens", "last_refill_ts")
        local tokens = tonumber(data[1])
        local last_refill = tonumber(data[2])

        if tokens == nil then
            tokens = capacity
            last_refill = now
        end

        local elapsed = math.max(0, now - last_refill)
        local refill = elapsed * refill_rate
        tokens = math.min(capacity, tokens + refill)

        local allowed = 0
        if tokens >= requested then
            tokens = tokens - requested
            allowed = 1
        end

        redis.call("HSET", key,
            "tokens", tokens,
            "last_refill_ts", now
        )

        redis.call("EXPIRE", key, ttl)

        return { allowed, tokens }
        """;

    private TokenBucketLuaScript() {}
}
