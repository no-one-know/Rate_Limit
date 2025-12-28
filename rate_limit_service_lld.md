# Rate Limit Service – Low Level Design (LLD)

## 1. Core Interfaces

### RateLimitAlgorithm
```java
public interface RateLimitAlgorithm {
    RateLimitResult execute(
        String redisKey,
        int capacity,
        int rateOrWindow,
        long timestampSeconds
    );
}
```

## 2. Algorithm Implementations
- **TokenBucketAlgorithm**: Uses Redis + Lua, supports bursts.
- **LeakyBucketAlgorithm**: Smooths traffic using a queue model.
- **FixedWindowAlgorithm**: Simple counter with TTL.
- **SlidingWindowAlgorithm**: Accurate rolling window using Redis ZSET + Lua.

## 3. Algorithm Resolution
A resolver selects the algorithm at startup based on configuration, with fallback to token-bucket.

## 4. Configuration Entities
- EndpointRateLimitPolicy
- PrimaryRateLimit
- SecondaryRateLimit

Algorithm-specific fields are strictly validated.

## 5. Normalized Runtime Model

### RateLimitExecutionConfig
```java
capacity
rateOrWindow
```

### ResolvedEndpointRateLimit
- primary execution config
- secondary execution config (optional)
- secondary metadata

## 6. Validation Layer
Validation ensures:
- Primary limit always exists
- Algorithm-specific fields match the selected algorithm
- No ambiguous or invalid configuration

Invalid configuration fails application startup.

## 7. Endpoint Resolver
Maps (HTTP method + path) to resolved runtime configuration. Performs lookup only at runtime.

## 8. Request Flow
1. Request enters filter chain
2. Endpoint is resolved
3. Primary limit is enforced
4. Secondary limit is enforced (if present)
5. Request is allowed or rejected

## 9. Redis Key Design
```
rl:{algorithm}:{scope}:{method}:{path}[:identifier]
```

## 10. Failure Handling
Handled centrally in RateLimiterService based on configured failure mode.

## 11. Concurrency
- Redis atomic operations
- Lua scripts prevent race conditions

## 12. Testing Strategy
- Unit tests for validation and resolution
- Integration tests with Redis
- Failure mode tests

## 13. Summary
This LLD defines a clean, extensible, and safe rate-limiting system ready for production use.
