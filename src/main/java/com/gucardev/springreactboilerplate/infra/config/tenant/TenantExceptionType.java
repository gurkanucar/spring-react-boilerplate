package com.gucardev.springreactboilerplate.infra.config.tenant;

import com.gucardev.springreactboilerplate.infra.exception.model.ExceptionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Tenant-isolation errors. Cross-tenant access is reported as a plain {@code NOT_FOUND} (status and
 * message) so it never reveals that a resource exists in another tenant.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TenantExceptionType {

    public static final ExceptionType NO_ORGANIZATION_CONTEXT =
            new ExceptionType("error.tenant.no_organization_context", HttpStatus.FORBIDDEN, "NO_ORGANIZATION_CONTEXT");

    public static final ExceptionType NO_WORKSPACE_CONTEXT =
            new ExceptionType("error.tenant.no_workspace_context", HttpStatus.BAD_REQUEST, "NO_WORKSPACE_CONTEXT");

    public static final ExceptionType CROSS_ORGANIZATION =
            new ExceptionType("error.tenant.cross_organization", HttpStatus.NOT_FOUND, "NOT_FOUND");

    public static final ExceptionType CROSS_WORKSPACE =
            new ExceptionType("error.tenant.cross_workspace", HttpStatus.NOT_FOUND, "NOT_FOUND");
}
