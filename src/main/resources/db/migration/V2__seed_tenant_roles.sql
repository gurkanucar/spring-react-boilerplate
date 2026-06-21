-- Tenant role tiers that drive per-feature tenant scoping (see TenantContext / the *Finder checks):
--   ADMIN          -> super-admin, bypasses tenant checks (sees every org/workspace)
--   ORG_MANAGER    -> organization-scoped, workspace optional (acts across the org's workspaces)
--   WORKSPACE_USER -> pinned to a single workspace
--   USER           -> standard end-user account
-- DataSeeder also ensures these at startup; seeding them here makes them exist at the DB level on a
-- fresh database independent of the runtime seeder. Idempotent: skips any role that already exists.
INSERT INTO roles (name, display_name, description, created_at, created_by)
SELECT v.name, v.display_name, v.description, now(), 'flyway'
FROM (VALUES
    ('USER', 'Regular User', 'Standard end-user account'),
    ('ADMIN', 'Administrator', 'Full system access'),
    ('ORG_MANAGER', 'Organization Manager', 'Manages workspaces within an organization'),
    ('WORKSPACE_USER', 'Workspace User', 'Workspace-level employee pinned to one workspace')
) AS v (name, display_name, description)
WHERE NOT EXISTS (SELECT 1 FROM roles r WHERE r.name = v.name);
