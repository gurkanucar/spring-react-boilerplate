# Feature Flags Usage

Per-workspace on/off toggles for optional product features (`features/core/featureflag`).

## Concepts

- A flag's **effective value** = the stored per-workspace override, else `false`.
- The flag **catalog** is `FeatureFlags.KNOWN` (constants like `NEWS_MODULE`, `IN_APP_NOTIFICATIONS`).
  Add a flag by declaring a constant and appending it to `KNOWN`. Ad-hoc string keys also work.
- New workspaces are seeded with all catalog flags **enabled** (via `WorkspaceCreatedEvent`).

## Gate a feature in code

```java
if (featureFlagService.isEnabled(workspaceId, FeatureFlags.NEWS_MODULE)) { ... }
```

`FeatureFlagService` (reusable by any feature):
- `isEnabled(workspaceId, key)` — hot path, cached.
- `effectiveMap(workspaceId)` / `list(workspaceId)` — all flags with values (admin UI).
- `set(workspaceId, key, enabled)` — upsert override.
- `enableDefaults(workspaceId)` / `deleteForWorkspace(workspaceId)` — lifecycle (wired to workspace events).

## Endpoints

`/api/v1/feature-flags` — `GET` (list with effective values) and `PUT /{key}` (toggle).
Roles: `ADMIN`, `ORG_MANAGER`.

## Caching

Reads are cached under `CacheNames.FEATURE_FLAGS` (`CAFFEINE_10M`); every write evicts the bucket.
In-memory per node — for multi-instance immediate propagation, switch the `cacheManager` on
`FeatureFlagService` to a `REDIS_*` manager (see `cache-usage.md`).

## Lifecycle wiring (decoupled)

`workspace` publishes `WorkspaceCreatedEvent` / `WorkspaceDeletedEvent` (in `features/shared/event`);
`FeatureFlagWorkspaceListener` seeds defaults / cleans up. No direct dependency between the features.
