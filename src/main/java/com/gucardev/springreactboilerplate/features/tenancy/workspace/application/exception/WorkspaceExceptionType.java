package com.gucardev.springreactboilerplate.features.tenancy.workspace.application.exception;

import com.gucardev.springreactboilerplate.infra.exception.model.ExceptionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Domain error catalog for workspaces. Throw via a constant, e.g.
 * {@code throw WorkspaceExceptionType.NOT_FOUND.toException(id);}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WorkspaceExceptionType {

    public static final ExceptionType NOT_FOUND =
            new ExceptionType("error.workspace.not_found", HttpStatus.NOT_FOUND, "WORKSPACE_NOT_FOUND");

    public static final ExceptionType SLUG_ALREADY_EXISTS =
            new ExceptionType("error.workspace.slug_exists", HttpStatus.CONFLICT, "WORKSPACE_SLUG_EXISTS");
}
