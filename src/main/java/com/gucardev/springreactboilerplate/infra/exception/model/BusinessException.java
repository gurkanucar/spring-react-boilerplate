package com.gucardev.springreactboilerplate.infra.exception.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Carries a business error as data: the {@link ExceptionType} it came from plus the message
 * arguments. The HTTP status, machine code and i18n message key are derived from the type;
 * the human-readable message is resolved at the HTTP boundary (GlobalExceptionHandler) using
 * the request locale.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final transient ExceptionType type;
    private final transient Object[] args;

    public BusinessException(ExceptionType type, Object... args) {
        super(type.key());
        this.type = type;
        this.args = args;
    }

    public HttpStatus getStatus() {
        return type.status();
    }

    public String getCode() {
        return type.code();
    }

    public String getMessageKey() {
        return type.key();
    }
}
