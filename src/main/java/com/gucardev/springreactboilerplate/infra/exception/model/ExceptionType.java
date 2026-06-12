package com.gucardev.springreactboilerplate.infra.exception.model;

import org.springframework.http.HttpStatus;

/**
 * A single business-error definition: its i18n message key, HTTP status and machine code.
 * The three fields live here once — domain catalogs just declare constants of this type
 * (see {@code CommonExceptionType}, {@code ExampleExceptionType}), no per-catalog boilerplate.
 *
 * <p>Throw straight from a catalog entry:
 * <pre>{@code throw ExampleExceptionType.NOT_FOUND.toException(id);}</pre>
 */
public record ExceptionType(String key, HttpStatus status, String code) {

    /**
     * Builds the {@link BusinessException} for this error. {@code args} fill the message
     * template ({@code {0}}, {@code {1}}, ...) and are resolved against the message bundle
     * by the exception handler, using the request locale.
     */
    public BusinessException toException(Object... args) {
        return new BusinessException(this, args);
    }
}
