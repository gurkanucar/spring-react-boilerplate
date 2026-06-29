package com.gucardev.springreactboilerplate.features.core.otp.application.exception;

import com.gucardev.springreactboilerplate.infra.exception.model.ExceptionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Domain-specific error catalog for the OTP slice. Throw via a constant, e.g.
 * {@code throw OtpExceptionType.INVALID_CODE.toException();}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OtpExceptionType {

    public static final ExceptionType NO_ACTIVE_OTP =
            new ExceptionType("error.otp.no_active", HttpStatus.NOT_FOUND, "OTP_NO_ACTIVE");

    public static final ExceptionType EXPIRED =
            new ExceptionType("error.otp.expired", HttpStatus.GONE, "OTP_EXPIRED");

    public static final ExceptionType INVALID_CODE =
            new ExceptionType("error.otp.invalid_code", HttpStatus.BAD_REQUEST, "OTP_INVALID_CODE");

    public static final ExceptionType MAX_ATTEMPTS_EXCEEDED =
            new ExceptionType("error.otp.max_attempts", HttpStatus.TOO_MANY_REQUESTS, "OTP_MAX_ATTEMPTS");

    public static final ExceptionType RESEND_TOO_SOON =
            new ExceptionType("error.otp.resend_too_soon", HttpStatus.TOO_MANY_REQUESTS, "OTP_RESEND_TOO_SOON");

    public static final ExceptionType NO_SENDER =
            new ExceptionType("error.otp.no_sender", HttpStatus.INTERNAL_SERVER_ERROR, "OTP_NO_SENDER");
}
