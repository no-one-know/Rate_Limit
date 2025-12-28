# Rate Limit Service – High Level Design (HLD)

## 1. Introduction
The Rate Limit Service is a Redis-backed, configurable rate-limiting system designed to protect APIs from abuse while remaining flexible, extensible, and algorithm-agnostic. It supports endpoint-level rate limits, hierarchical limits (primary and secondary), and multiple rate-limiting algorithms selectable via configuration.

## 2. Goals
- Enforce rate limits per HTTP method and path
- Support multiple algorithms:
  - Token Bucket
  - Leaky Bucket
  - Fixed Window
  - Sliding Window
- Support primary (API-level) and secondary (identifier-level) limits
- Ensure atomic enforcement using Redis
- Provide fail-open and fail-close behavior

## 3. Non-Goals
- No UI or dashboard
- No per-request algorithm switching
- No persistence beyond Redis

## 4. High-Level Architecture

Client Request  
→ Logging Filter  
→ Rate Limiting Filter  
→ Endpoint Resolver  
→ RateLimiter Service  
→ Selected RateLimit Algorithm  
→ Redis

## 5. Key Design Decisions
- **Opt-in rate limiting**: Only configured endpoints are limited.
- **Algorithm pluggability**: Algorithms are isolated behind a common interface.
- **Fail-fast validation**: Configuration errors fail at startup.
- **Redis as source of truth**: All counters and state are stored in Redis.

## 6. Configuration Overview

```yaml
rate-limiter:
  enabled: true
  failure-mode: FAIL_OPEN
  algorithm: sliding-window

  endpoints:
    - method: POST
      path: /users
      primary:
        capacity: 100
        time-window: 60
```

## 7. Failure Modes
- **FAIL_OPEN**: Allow requests if Redis fails.
- **FAIL_CLOSE**: Reject requests if Redis fails.

## 8. Scalability
- Stateless application nodes
- Horizontal scaling supported
- Redis handles concurrency

## 9. Security
- Deterministic, namespaced Redis keys
- No sensitive data persisted

## 10. Extensibility
- Easy addition of new algorithms
- Support for new identifier sources
- Future per-endpoint algorithm overrides

## 11. Summary
The system provides a robust, production-ready rate-limiting solution with strong separation of concerns and long-term extensibility.
