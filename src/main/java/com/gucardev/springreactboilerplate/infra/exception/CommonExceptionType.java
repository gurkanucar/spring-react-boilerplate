package com.gucardev.springreactboilerplate.infra.exception;

import com.gucardev.springreactboilerplate.infra.exception.model.ExceptionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Cross-cutting business errors reusable by any domain. Throw via a constant, e.g.
 * {@code throw CommonExceptionType.NOT_FOUND.toException(entity, id);}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonExceptionType {

    public static final ExceptionType DEFAULT =
            new ExceptionType("error.default", HttpStatus.BAD_REQUEST, "DEFAULT");

    public static final ExceptionType NOT_FOUND =
            new ExceptionType("error.not_found", HttpStatus.NOT_FOUND, "NOT_FOUND");

    public static final ExceptionType ALREADY_EXISTS =
            new ExceptionType("error.already_exists", HttpStatus.CONFLICT, "ALREADY_EXISTS");

    public static final ExceptionType VALIDATION_FAILED =
            new ExceptionType("error.validation_failed", HttpStatus.BAD_REQUEST, "VALIDATION_FAILED");

    public static final ExceptionType FORBIDDEN =
            new ExceptionType("error.auth.forbidden", HttpStatus.FORBIDDEN, "ACCESS_DENIED");
}
