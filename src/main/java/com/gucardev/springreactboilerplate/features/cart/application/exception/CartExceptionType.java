package com.gucardev.springreactboilerplate.features.cart.application.exception;

import com.gucardev.springreactboilerplate.infra.exception.model.ExceptionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Application-level error catalog for carts (as opposed to the domain rule violations the aggregate
 * raises itself). Throw via a constant, e.g. {@code throw CartExceptionType.NOT_FOUND.toException(id);}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CartExceptionType {

    public static final ExceptionType NOT_FOUND =
            new ExceptionType("error.cart.not_found", HttpStatus.NOT_FOUND, "CART_NOT_FOUND");
}
