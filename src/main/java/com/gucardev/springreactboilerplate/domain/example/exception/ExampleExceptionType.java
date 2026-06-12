package com.gucardev.springreactboilerplate.domain.example.exception;

import com.gucardev.springreactboilerplate.infra.exception.model.ExceptionType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Domain-specific error codes for the example slice. Resolved to messages + HTTP status by
 * {@link com.gucardev.springreactboilerplate.infra.exception.ExceptionUtil}.
 */
@Getter
@RequiredArgsConstructor
public enum ExampleExceptionType implements ExceptionType {

    NOT_FOUND("error.example.not_found", HttpStatus.NOT_FOUND, "EXAMPLE_NOT_FOUND");

    private final String key;
    private final HttpStatus status;
    private final String code;
}
