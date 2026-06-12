package com.gucardev.springreactboilerplate.domain.example.exception;

import com.gucardev.springreactboilerplate.infra.exception.model.ExceptionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Domain-specific error catalog for the example slice. Throw via a constant, e.g.
 * {@code throw ExampleExceptionType.NOT_FOUND.toException(id);}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExampleExceptionType {

    public static final ExceptionType NOT_FOUND =
            new ExceptionType("error.example.not_found", HttpStatus.NOT_FOUND, "EXAMPLE_NOT_FOUND");
}
