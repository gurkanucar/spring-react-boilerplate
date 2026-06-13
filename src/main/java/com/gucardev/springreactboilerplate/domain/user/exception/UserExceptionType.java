package com.gucardev.springreactboilerplate.domain.user.exception;

import com.gucardev.springreactboilerplate.infra.exception.model.ExceptionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Domain-specific error catalog for the user slice. Throw via a constant, e.g.
 * {@code throw UserExceptionType.NOT_FOUND.toException(id);}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserExceptionType {

    public static final ExceptionType NOT_FOUND =
            new ExceptionType("error.user.not_found", HttpStatus.NOT_FOUND, "USER_NOT_FOUND");

    public static final ExceptionType EMAIL_ALREADY_EXISTS =
            new ExceptionType("error.auth.email_already_exists", HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS");

    public static final ExceptionType ROLE_NOT_FOUND =
            new ExceptionType("error.user.role_not_found", HttpStatus.BAD_REQUEST, "ROLE_NOT_FOUND");

    public static final ExceptionType ORGANIZATION_NOT_FOUND =
            new ExceptionType("error.user.organization_not_found", HttpStatus.BAD_REQUEST, "USER_ORGANIZATION_NOT_FOUND");

    public static final ExceptionType WORKSPACE_REQUIRES_ORGANIZATION =
            new ExceptionType("error.user.workspace_requires_organization", HttpStatus.BAD_REQUEST, "USER_WORKSPACE_REQUIRES_ORG");

    public static final ExceptionType WORKSPACE_NOT_IN_ORGANIZATION =
            new ExceptionType("error.user.workspace_not_in_organization", HttpStatus.BAD_REQUEST, "USER_WORKSPACE_NOT_IN_ORG");
}
