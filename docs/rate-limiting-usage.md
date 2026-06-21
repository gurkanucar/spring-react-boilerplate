# Rate Limiting Usage

Per-IP token-bucket throttling for sensitive endpoints (`infra/config/ratelimit`), using bucket4j.

## How it works

`RateLimitFilter` (a `OncePerRequestFilter`, highest precedence) matches the request path against the
configured patterns; for each `(clientIp, path)` it consumes a token from a bucket of `capacity`
tokens that refills fully every `refillSeconds`. Buckets live in an in-memory Caffeine cache.

Over-limit requests get **HTTP 429** in the standard `ApiError` envelope
(`businessErrorCode: RATE_LIMIT_EXCEEDED`) plus a `Retry-After` header.

## Configuration

```yaml
app.rate-limit:
  enabled: ${RATE_LIMIT_ENABLED:true}
  capacity: ${RATE_LIMIT_CAPACITY:20}        # requests per window, per IP per path
  refill-seconds: ${RATE_LIMIT_REFILL_SECONDS:60}
  paths:                                     # ant patterns
    - /auth/login
    - /auth/register
    - /otp/send
```

Add endpoints by extending `paths`. Client IP honors the first `X-Forwarded-For` value (set it on
your proxy), else `RemoteAddr`.

## Notes & extension

- **In-memory per node.** For a shared limit across instances, back the buckets with bucket4j's
  Redis/Hazelcast `ProxyManager` instead of the Caffeine map.
- Tests disable it (`app.rate-limit.enabled=false`); `RateLimitTest` re-enables it with a tiny
  capacity via `@TestPropertySource`.
