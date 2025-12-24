# Rate Limit Service – Low Level Design (LLD)

## 1. Introduction

This document describes the **Low Level Design (LLD)** of the Rate Limit Service. It expands on the HLD and captures **all architectural decisions, data models, algorithms, and implementation details**.

The LLD is intended to:
- Enable accurate implementation
- Serve as long-term documentation
- Provide interview- and production-grade clarity

---

## 2. Core Design Principles

- Stateless Java services
- Redis as the single source of coordination
- Atomicity via Lua scripting
- Configuration-driven behavior
- Fail-open safety

---

## 3. Rate Limiting Model

### 3.1 Token Bucket State

Each bucket contains:
- `tokens`: current available tokens
- `last_refill_ts`: last refill timestamp (epoch seconds)

### 3.2 Refill Formula

```
elapsed = now - last_refill_ts
new_tokens = elapsed × refill_rate
tokens = min(capacity, tokens + new_tokens)
```

---

## 4. Redis Data Model

### 4.1 Key Structure

```
rl:{strategy}:{identifier}
```

Examples:
- `rl:ip:192.168.1.10`
- `rl:api:/v1/orders`
- `rl:apikey:abc123`

### 4.2 Redis Hash Fields

```
tokens
last_refill_ts
```

---

## 5. Atomicity Strategy

### 5.1 Why Lua Scripting

Token bucket updates involve multiple steps:
- Read state
- Compute refill
- Deduct token
- Write state

Lua scripts ensure:
- Single-threaded execution inside Redis
- No interleaving of commands
- Zero race conditions

---

## 6. Lua Script Contract

### Inputs
- `KEYS[1]`: Redis bucket key
- `ARGV[1]`: capacity
- `ARGV[2]`: refill rate
- `ARGV[3]`: current timestamp
- `ARGV[4]`: tokens requested

### Outputs
- `allowed` (0 or 1)
- `remaining_tokens`

---

## 7. Lua Script Logic (Step-by-Step)

1. Load bucket state from Redis
2. Initialize bucket if missing
3. Calculate elapsed time
4. Refill tokens
5. Clamp tokens to capacity
6. Check availability
7. Persist updated state
8. Return decision

All steps are executed atomically.

---

## 8. Java Component Design

### 8.1 Core Interfaces

- `RateLimiter`
- `RedisRateLimiter`
- `RateLimitKeyStrategy`

Each interface isolates a responsibility and improves testability.

---

## 9. RequestContext Abstraction

`RequestContext` encapsulates:
- IP address
- API path
- API key
- User identifier
- Timestamp

This decouples rate limiting from HTTP frameworks.

---

## 10. Strategy Design

Each strategy:
- Resolves a Redis key
- Has its own configuration
- Is independently enabled/disabled

Strategies implemented:
- IP-based
- API-based
- API-key-based

Multiple strategies may apply to a single request.

---

## 11. Strategy Evaluation Flow

```
For each enabled strategy:
  → Resolve key
  → Execute Lua script
  → Reject immediately if limit exceeded
```

All strategies must allow the request for it to proceed.

---

## 12. RedisRateLimiter Design

Responsibilities:
- Load Lua script
- Execute via `EVALSHA`
- Parse result
- Return domain-level decision

Redis details are hidden from upper layers.

---

## 13. Failure Handling

### 13.1 Fail-Open Behavior

If Redis execution fails:
- Request is allowed
- Error is logged

This behavior is configurable.

---

## 14. Thread Safety and Concurrency

- Java layer is stateless
- Redis Lua scripts provide atomicity
- No Java locks required

Concurrency safety is guaranteed by design.

---

## 15. HTTP Filter Integration

The rate limiter is integrated via a Servlet filter:
- Builds `RequestContext`
- Invokes `RateLimiterService`
- Returns HTTP 429 on rejection

This ensures transparent enforcement.

---

## 16. Configuration Binding

YAML configuration is mapped to:
- `RateLimiterProperties`
- `StrategyProperties`

Misconfiguration fails fast at startup.

---

## 17. Extensibility Considerations

The design supports:
- New rate-limit strategies
- Different algorithms (future)
- Per-endpoint customization

No core refactoring is required for extensions.

---

## 18. Performance Characteristics

- O(1) per request
- Single Redis round trip
- Minimal Java overhead

Scales with Redis throughput.

---

## 19. Operational Considerations

- Redis persistence (AOF) protects state
- Buckets naturally expire if unused
- Metrics can be added without redesign

---

## 20. Summary

The Low Level Design ensures:
- Correctness under concurrency
- Distributed safety
- High availability
- Clean layering and extensibility

Together with the HLD, this LLD fully specifies a production-grade Rate Limit Service.

