# Rate Limit Service – High Level Design (HLD)

## 1. Introduction

This document describes the **High Level Design (HLD)** of the **Rate Limit Service** built using **Java, Redis, and the Token Bucket algorithm**. The goal of this service is to protect backend APIs from abuse, smooth traffic spikes, and ensure fair usage across clients while remaining highly available and scalable.

The design intentionally prioritizes:
- Horizontal scalability
- Low latency
- Simplicity of reasoning
- Operational safety

Strict financial-grade accuracy is **not** a requirement; availability and performance are preferred.

---

## 2. Problem Statement

Modern backend systems are exposed to:
- Excessive client requests
- Abuse (intentional or accidental)
- Traffic bursts

Without rate limiting:
- Downstream services may degrade
- Databases may become overloaded
- Legitimate users may be impacted

A centralized, distributed-safe rate limiting mechanism is required.

---

## 3. Goals and Non-Goals

### 3.1 Goals

- Enforce request limits per client
- Support burst traffic within bounds
- Work correctly in distributed environments
- Be configurable without code changes
- Integrate transparently with HTTP APIs

### 3.2 Non-Goals

- Billing or monetization
- Authentication or authorization
- Exact-once semantics
- Financial correctness

---

## 4. Algorithm Choice

### 4.1 Token Bucket Algorithm

The Token Bucket algorithm was chosen because:
- It allows short bursts of traffic
- It smooths long-term request rate
- It is widely used and well understood

Each client is assigned a bucket:
- Bucket has a maximum capacity
- Tokens are refilled at a fixed rate
- Each request consumes one token

Requests are rejected when no tokens remain.

---

## 5. Technology Choices

### 5.1 Redis

Redis is used as the centralized coordination store because:
- It is in-memory and fast
- It provides atomic operations
- It supports Lua scripting
- It is widely deployed and battle-tested

### 5.2 Java

Java is used for:
- Strong typing
- Mature ecosystem
- Excellent Redis clients
- Easy Spring integration

---

## 6. Failure Model

### 6.1 Fail-Open Strategy

If Redis is unavailable:
- Requests are **allowed**
- Rate limiting is temporarily bypassed

Rationale:
- Rate limiting is a protective mechanism
- Blocking legitimate traffic is worse than allowing excess traffic

---

## 7. Configurability

The rate limiter is configuration-driven via YAML:
- Enable or disable the entire limiter
- Configure per-strategy limits
- Change limits without redeploying code

Supported strategies:
- IP-based
- API-path-based
- API-key-based
- User-based (future extension)

---

## 8. High-Level Architecture

```
Client
  ↓
HTTP Filter / Interceptor
  ↓
Rate Limiter Service (Java)
  ↓
Redis (Token Buckets)
```

The Rate Limiter acts as a gatekeeper before business logic is executed.

---

## 9. Request Flow (HLD)

1. HTTP request arrives
2. Request context is constructed (IP, API, API key, timestamp)
3. Configured rate-limit strategies are evaluated
4. Redis is consulted via Lua script
5. Request is allowed or rejected

---

## 10. Scalability Characteristics

- Java service is stateless → horizontal scaling
- Redis handles atomicity → no coordination between instances
- Lua scripts ensure correctness under concurrency

---

## 11. Observability (Planned)

The system is designed to support:
- Metrics for allowed / rejected requests
- Per-strategy visibility
- Redis health monitoring

---

## 12. Summary

The High Level Design provides:
- A scalable, distributed-safe rate limiting system
- Clear separation of concerns
- Configuration-driven behavior
- High availability via fail-open strategy

This HLD forms the foundation for the detailed Low Level Design.

