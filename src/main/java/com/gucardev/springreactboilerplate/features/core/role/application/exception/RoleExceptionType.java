package com.gucardev.springreactboilerplate.features.core.role.application.exception;

import com.gucardev.springreactboilerplate.infra.exception.model.ExceptionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Domain-specific error catalog for the role slice. Throw via a constant, e.g.
 * {@code throw RoleExceptionType.NOT_FOUND.toException(id);}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RoleExceptionType {

    public static final ExceptionType NOT_FOUND =
            new ExceptionType("error.role.not_found", HttpStatus.NOT_FOUND, "ROLE_NOT_FOUND");

    public static final ExceptionType NAME_ALREADY_EXISTS =
            new ExceptionType("error.role.name_already_exists", HttpStatus.CONFLICT, "ROLE_NAME_ALREADY_EXISTS");
}
