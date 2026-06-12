# Cache Usage

The cache setup has two axes you choose independently:

- **Cache name** (`CacheNames`) — a logical bucket of entries. **Names only — no TTL.**
- **Cache manager** (`CacheManagers`) — picks the **backing store** *and* the **TTL**.

You combine them on each method:

```java
@Cacheable(cacheNames = CacheNames.USERS, cacheManager = CacheManagers.REDIS_10M)
public User findById(Long id) { ... }
```

> The TTL comes from the **manager**, not the name. The same `CacheNames.USERS` bucket can live
> 30 seconds under `CAFFEINE_30S` or 1 hour under `REDIS_1H`.

All config lives in `com.gucardev.springreactboilerplate.config.cache`.

---

## Cache managers (store + TTL)

One manager per (store, TTL). Reference them by constant via `cacheManager = ...`.

| Caffeine (in-memory, per instance) | Redis (shared, distributed) | TTL |
|------------------------------------|-----------------------------|-----|
| `CacheManagers.CAFFEINE_30S` | `CacheManagers.REDIS_30S` | 30 seconds |
| `CacheManagers.CAFFEINE_1M`  | `CacheManagers.REDIS_1M`  | 1 minute |
| `CacheManagers.CAFFEINE_3M`  | `CacheManagers.REDIS_3M`  | 3 minutes |
| `CacheManagers.CAFFEINE_5M` *(default / `@Primary`)* | `CacheManagers.REDIS_5M` | 5 minutes |
| `CacheManagers.CAFFEINE_10M` | `CacheManagers.REDIS_10M` | 10 minutes |
| `CacheManagers.CAFFEINE_30M` | `CacheManagers.REDIS_30M` | 30 minutes |
| `CacheManagers.CAFFEINE_1H`  | `CacheManagers.REDIS_1H`  | 1 hour |

- **Caffeine** = fastest, no network, per-instance, lost on restart. TTL is `expireAfterWrite`.
- **Redis** = shared across all instances, survives restarts, costs a network hop + JSON (de)serialization. TTL is `entryTtl`.
- If you omit `cacheManager` entirely, **`CAFFEINE_5M`** is used (it's `@Primary`).

---

## Cache names

Names only. Caches are created on demand, so adding one is just declaring a constant in `CacheNames`.

| Constant | Cache name |
|----------|------------|
| `CacheNames.USERS`    | `users` |
| `CacheNames.ROLES`    | `roles` |
| `CacheNames.SETTINGS` | `settings` |

Add your own:

```java
public static final String PRODUCTS = "products";
```

There's nothing else to register — pick whichever manager gives the TTL you want.

---

## Using it on methods

Caching is enabled globally (`@EnableCaching` in `CachingConfig`). Use Spring's standard
annotations and pass the name + manager via constants.

### `@Cacheable` — read-through

```java
import org.springframework.cache.annotation.Cacheable;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheNames;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheManagers;

@Service
public class UserService {

    // Redis, "users" bucket, 10-minute TTL. Key = the id argument.
    @Cacheable(cacheNames = CacheNames.USERS, cacheManager = CacheManagers.REDIS_10M, key = "#id")
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    // Caffeine, "settings" bucket, 30-second TTL.
    @Cacheable(cacheNames = CacheNames.SETTINGS, cacheManager = CacheManagers.CAFFEINE_30S)
    public Settings current() {
        return loadSettings();
    }

    // No manager specified -> falls back to CAFFEINE_5M (the @Primary).
    @Cacheable(cacheNames = CacheNames.ROLES)
    public List<Role> allRoles() {
        return roleRepository.findAll();
    }
}
```

### `@CachePut` — always run, then refresh the cache

```java
@CachePut(cacheNames = CacheNames.USERS, cacheManager = CacheManagers.REDIS_10M, key = "#user.id")
public User update(User user) {
    return userRepository.save(user);
}
```

### `@CacheEvict` — remove entries

```java
// One entry
@CacheEvict(cacheNames = CacheNames.USERS, cacheManager = CacheManagers.REDIS_10M, key = "#id")
public void delete(Long id) {
    userRepository.deleteById(id);
}

// Whole bucket
@CacheEvict(cacheNames = CacheNames.USERS, cacheManager = CacheManagers.REDIS_10M, allEntries = true)
public void reindexUsers() { ... }
```

> A cache name is independent per manager. If you write under `REDIS_10M` you must evict
> under `REDIS_10M` — evicting the same name via `CAFFEINE_30S` touches a different store.
> Pick one (manager, name) pair per logical cache and use it consistently.

### Programmatic access (no annotations)

Inject the manager by bean name:

```java
@Service
public class ManualCacheExample {

    private final CacheManager redis10m;

    public ManualCacheExample(@Qualifier(CacheManagers.REDIS_10M) CacheManager redis10m) {
        this.redis10m = redis10m;
    }

    public User get(Long id) {
        Cache cache = redis10m.getCache(CacheNames.USERS);
        return cache != null ? cache.get(id, User.class) : null;
    }
}
```

---

## Keys

- **Cache key**: defaults to the method arguments. Override with SpEL: `key = "#id"` or
  `key = "#user.id"`. Prefer an explicit `key` when there are multiple args.
- **Redis key prefix**: every Redis key is prefixed with the shared `app.redis.key-prefix`
  namespace (the same one used for all Redis keys, e.g. OTP) so it won't collide with other
  apps on a shared Redis. Empty by default; set it (include a trailing separator, e.g.
  `myapp:`) via `app.redis.key-prefix` / the `REDIS_KEY_PREFIX` env var.

Example stored Redis key for `findById(123)` under `REDIS_10M` with `app.redis.key-prefix=myapp:`:

```
myapp:users::123
```

With an empty prefix (the default) the key is simply `users::123`.

Caffeine is in-memory and per-instance, so it needs no prefix.

> Redis values are stored as JSON with an embedded `@class` type hint so they deserialize
> back to the concrete type. Keep cached types concrete (avoid anonymous/local classes)
> and round-trippable.

---

## Configuration reference

`application.yml`:

```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      timeout: 4s
app:
  redis:
    # Shared prefix for ALL Redis keys (cache + OTP/etc.). Empty by default.
    key-prefix: ${REDIS_KEY_PREFIX:}
```

The Redis managers build lazily — the app starts even if Redis is down, and connects on
first cache use.

### Files

| File | Responsibility |
|------|----------------|
| `CachingConfig` | `@EnableCaching` |
| `CacheNames` | Logical cache names (names only — extend this) |
| `CacheManagers` | Bean names of the 14 managers (store × TTL) |
| `CaffeineCacheConfig` | 7 Caffeine managers (`CAFFEINE_5M` is `@Primary`) |
| `RedisCacheConfig` | 7 Redis managers + key prefix + JSON serialization |
