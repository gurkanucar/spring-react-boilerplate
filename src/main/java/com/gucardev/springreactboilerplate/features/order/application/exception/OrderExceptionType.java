package com.gucardev.springreactboilerplate.features.order.application.exception;

import com.gucardev.springreactboilerplate.infra.exception.model.ExceptionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Domain error catalog for orders. Throw via a constant, e.g.
 * {@code throw OrderExceptionType.NOT_FOUND.toException(id);}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OrderExceptionType {

    public static final ExceptionType NOT_FOUND =
            new ExceptionType("error.order.not_found", HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND");
}
