# Package Structure

Top-level layout under `com.gucardev.springreactboilerplate`:

```
infra/                 cross-cutting platform code (not a business feature)
  config/              config, security, tenant, cache, mail, ratelimit, async, scheduling, ...
  exception/           global handler, ApiError envelope, ExceptionType/BusinessException
  seed/                DataSeeder (default roles + admin)
features/              all business + built-in features (package-by-feature, vertical slices)
  core/                built-in platform features — keep these
    auth/ otp/ user/ role/ file/ featureflag/ notification/
  tenancy/             the multi-tenant building blocks
    organization/ workspace/
  news/                example workspace-scoped business feature
  example/             sample feature — delete/replace in a real project
  shared/              cross-feature shared code
    entity/BaseEntity  repository/BaseJpaRepository + specification/BaseSpecification
    dto/ (BaseDto, BaseFilterRequest)  util/ (SlugUtil)  event/ (cross-feature events)
```

## Conventions

- **Package-by-feature, not by layer.** Each feature owns its full vertical slice:
  `controller/ service/ (service/usecase/) repository/ (repository/specification/) entity/ mapper/ model/dto/ model/request/ exception/`.
- **One `@Service` per use case** (`CreateXUseCase`, `GetAllXUseCase`, ...) plus an `XFinder` for
  tenant-aware "fetch-or-404". A plain `XService` is used only for genuinely reusable domain logic
  (e.g. `FeatureFlagService`, `NotificationService`).
- **DTOs use boxed types** (`Boolean`, `Integer`, ...), never primitives — see entity/DTO fields.
- **Cross-feature communication goes through events** in `features/shared/event` (e.g.
  `WorkspaceCreatedEvent`), so features don't import each other. `featureflag`/`notification` are the
  exception: they are platform services other features may call directly.

## Where do I add a new feature?

- Built-in/platform capability every project keeps → `features/core/<name>`.
- A real business domain → `features/<name>` (top level), workspace-scoped if multi-tenant.
- Tenant structure itself → `features/tenancy/<name>` (rare).
- Throwaway sample → mirror `features/example` and delete later.

Copy `features/tenancy/workspace` (UUID id, tenant-scoped) or `features/news` (workspace-scoped +
slug + element collections) as the closest templates.
