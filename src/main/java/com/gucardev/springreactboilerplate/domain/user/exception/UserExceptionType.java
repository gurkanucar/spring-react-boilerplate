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
}
