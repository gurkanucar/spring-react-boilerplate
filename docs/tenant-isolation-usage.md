# Tenant isolation

Multi-tenancy is enforced **per feature**, explicitly and without ORM magic:

- **List/search queries** are scoped in the feature's `*Specification` (e.g. `NewsSpecification`
  adds `workspace_id = :workspaceId`), with the tenant id read from `TenantContextHolder` in the
  use case.
- **Id lookups** are scoped in the feature's `*Finder`, which loads the row then checks it belongs
  to the caller's tenant — reporting a cross-tenant hit as `NOT_FOUND` so it never reveals the row
  exists in another tenant.

## Tenant hierarchy and roles

`TenantContextFilter` builds a `TenantContext` (organization, optional workspace, super-admin flag)
from the authenticated principal and the `X-Workspace-Id` header. The role tiers (seeded by
`DataSeeder` and `V2__seed_tenant_roles.sql`):

| Role | Scope |
|------|-------|
| `ADMIN` | super-admin — bypasses tenant checks, sees everything |
| `ORG_MANAGER` | organization-scoped, workspace optional (acts across the org's workspaces) |
| `WORKSPACE_USER` | pinned to a single workspace |

Finders honor this: `WorkspaceFinder` lets a super-admin through and otherwise requires the row's
`organizationId` to match; `NewsFinder` requires the active workspace to match.

## Adding a tenant-scoped feature

1. Scope the list query in `ThingSpecification` (add `equals("workspaceId", workspaceId)` or
   `equals("organizationId", organizationId)`), reading the tenant id from `TenantContextHolder`
   in the use case.
2. Scope id lookups in `ThingFinder`: load, then verify ownership against `TenantContextHolder`
   (mirror `NewsFinder` / `WorkspaceFinder` / `NotificationFinder`).

Endpoints that only super-admins use (Organization, User, Role) are `hasRole('ADMIN')` and are
intentionally not tenant-scoped — admins see everything.

## Writes

Tenant scoping above covers reads. Validate tenant ownership on create/update too (e.g.
`UserTenantAssignmentValidator`) so a caller cannot write a row into another tenant.

## Tests

`NewsTenantTest` and `WorkspaceTenantIsolationTest` prove list + id lookups are scoped and that
cross-tenant access is reported as `NOT_FOUND`.
