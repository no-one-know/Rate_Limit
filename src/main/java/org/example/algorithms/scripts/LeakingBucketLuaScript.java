package org.example.algorithms.scripts;

public final class LeakingBucketLuaScript {

    public static final String SCRIPT = """
            local key = KEYS[1]
            local capacity = tonumber(ARGV[1])
            local leak_rate = tonumber(ARGV[2])
            local now = tonumber(ARGV[3])
            local ttl = tonumber(ARGV[4])
            
            local data = redis.call("HMGET", key, "queue", "ts")
            
            local queue = tonumber(data[1]) or 0
            local last_ts = tonumber(data[2]) or now
            
            local elapsed = math.max(0, now - last_ts)
            local leaked = elapsed * leak_rate
            
            queue = math.max(0, queue - leaked)
            
            local allowed = 0
            if queue < capacity then
                queue = queue + 1
                allowed = 1
            end
            
            redis.call("HMSET", key, "queue", queue, "ts", now)
            redis.call("EXPIRE", key, ttl)
            
            return { allowed, queue }
            """;

    private LeakingBucketLuaScript() {}
}
