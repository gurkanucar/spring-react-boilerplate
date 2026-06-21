# Rate Limiting Usage

Rate limiting uses **resilience4j's `RateLimiter`** as the engine (already on the classpath via
`spring-cloud-starter-circuitbreaker-resilience4j`). There are three layers, each for a different
need. All return **HTTP 429** in the standard `ApiError` envelope.

> The engine is **in-memory, per node**: each instance keeps its own counters, so behind N instances
> the effective limit is N× the configured value. That is fine for a single instance or soft limits.

## 1. Per-IP, pre-auth (servlet filter)

`RateLimitFilter` throttles sensitive endpoints before authentication (brute-force / OTP abuse),
keyed by client IP. It runs at highest precedence and holds one limiter per `(IP, path)` in an
idle-evicting Caffeine cache.

```yaml
app:
  rate-limit:
    enabled: ${RATE_LIMIT_ENABLED:true}
    capacity: ${RATE_LIMIT_CAPACITY:100}   # requests per window, per IP per path
    refill-seconds: ${RATE_LIMIT_REFILL_SECONDS:60}
    paths:
      - /auth/login
      - /auth/register
      - /otp/send
```

Generous capacity on purpose: many users can share one public IP (NAT). Disabled in tests
(`app.rate-limit.enabled=false`). Client IP honors the first `X-Forwarded-For` hop, else remote addr.

## 2. Global per-endpoint (resilience4j built-in `@RateLimiter`)

For a shared cap on a method (e.g. protecting a downstream resource), use resilience4j's built-in
annotation — **name-based / global**, not per caller:

```java
@RateLimiter(name = "markAllNotificationsRead")
public int execute() { ... }
```
```yaml
resilience4j:
  ratelimiter:
    instances:
      markAllNotificationsRead:
        limit-for-period: 60
        limit-refresh-period: 60s
        timeout-duration: 0s        # reject immediately (429) instead of blocking
```

Over-limit throws `RequestNotPermitted`, mapped to 429 by `GlobalExceptionHandler`.

## 3. Per-user (or any dynamic key) — `KeyedRateLimiter`

resilience4j's annotation can't key by user/argument, so per-user limits are applied
programmatically with a one-line call (no custom annotation):

```java
keyedRateLimiter.acquireForUser("createNews", userId, 20, 60);  // 20 / minute per user
// or any key:
keyedRateLimiter.acquire("tenant:" + tenantId, 1000, 60);
```

Applied in `CreateNewsUseCase` (20 news/min per user; skipped when unauthenticated). Throws
`RequestNotPermitted` → 429. Limiters are held per key in an idle-evicting Caffeine cache.
