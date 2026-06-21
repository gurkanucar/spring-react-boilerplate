package com.gucardev.springreactboilerplate.features.core.auth.exception;

import com.gucardev.springreactboilerplate.infra.exception.model.ExceptionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Auth-specific error catalog. Bad-credentials and disabled-account cases are not here — those
 * surface as Spring Security exceptions handled centrally in {@code GlobalExceptionHandler}.
 * Throw via a constant, e.g. {@code throw AuthExceptionType.EMAIL_ALREADY_EXISTS.toException(email);}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthExceptionType {

    public static final ExceptionType EMAIL_ALREADY_EXISTS =
            new ExceptionType("error.auth.email_already_exists", HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS");

    public static final ExceptionType INVALID_REFRESH_TOKEN =
            new ExceptionType("error.auth.invalid_refresh_token", HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN");

    public static final ExceptionType REFRESH_TOKEN_EXPIRED =
            new ExceptionType("error.auth.refresh_token_expired", HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_EXPIRED");

    public static final ExceptionType USER_NOT_FOUND =
            new ExceptionType("error.auth.user_not_found", HttpStatus.NOT_FOUND, "USER_NOT_FOUND");

    public static final ExceptionType ROLE_NOT_FOUND =
            new ExceptionType("error.auth.role_not_found", HttpStatus.INTERNAL_SERVER_ERROR, "ROLE_NOT_FOUND");
}
