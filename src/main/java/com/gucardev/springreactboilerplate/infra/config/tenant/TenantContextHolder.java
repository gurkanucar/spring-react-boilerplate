package com.gucardev.springreactboilerplate.infra.config.tenant;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Thread-local access to the current request's {@link TenantContext}, mirroring Spring Security's
 * {@code SecurityContextHolder}. Populated by {@code TenantContextFilter} and cleared at the end of
 * the request. Finders/specifications read it to scope queries to the caller's tenant.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TenantContextHolder {

    private static final ThreadLocal<TenantContext> HOLDER = new ThreadLocal<>();

    public static void set(TenantContext context) {
        HOLDER.set(context);
    }

    public static TenantContext get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }

    /** The caller's organization id, or throws if there is no organization context (and not super-admin). */
    public static UUID requireOrganizationId() {
        TenantContext context = HOLDER.get();
        if (context == null || (!context.hasOrganization() && !context.superAdmin())) {
            throw TenantExceptionType.NO_ORGANIZATION_CONTEXT.toException();
        }
        return context.organizationId();
    }

    /** The active workspace id, or throws if no {@code X-Workspace-Id} was provided. */
    public static UUID requireWorkspaceId() {
        TenantContext context = HOLDER.get();
        if (context == null || !context.hasWorkspace()) {
            throw TenantExceptionType.NO_WORKSPACE_CONTEXT.toException();
        }
        return context.workspaceId();
    }

    public static boolean isSuperAdmin() {
        TenantContext context = HOLDER.get();
        return context != null && context.superAdmin();
    }
}
