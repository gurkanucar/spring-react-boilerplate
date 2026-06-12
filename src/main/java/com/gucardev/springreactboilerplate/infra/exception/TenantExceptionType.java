package com.gucardev.springreactboilerplate.infra.exception;

import com.gucardev.springreactboilerplate.infra.exception.model.ExceptionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Multi-tenancy / workspace-scoping business errors.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TenantExceptionType {

    public static final ExceptionType NO_WORKSPACE_CONTEXT =
            new ExceptionType("error.tenant.no_workspace_context", HttpStatus.FORBIDDEN, "NO_WORKSPACE_CONTEXT");

    public static final ExceptionType CROSS_WORKSPACE =
            new ExceptionType("error.tenant.cross_workspace", HttpStatus.NOT_FOUND, "CROSS_WORKSPACE");

    public static final ExceptionType NO_ORGANIZATION_CONTEXT =
            new ExceptionType("error.tenant.no_organization_context", HttpStatus.FORBIDDEN, "NO_ORGANIZATION_CONTEXT");

    public static final ExceptionType CROSS_ORGANIZATION =
            new ExceptionType("error.tenant.cross_organization", HttpStatus.NOT_FOUND, "CROSS_ORGANIZATION");
}
