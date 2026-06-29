package com.gucardev.springreactboilerplate.features.core.notification.application.exception;

import com.gucardev.springreactboilerplate.infra.exception.model.ExceptionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Domain error catalog for notifications. Throw via a constant, e.g.
 * {@code throw NotificationExceptionType.NOT_FOUND.toException(id);}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotificationExceptionType {

    public static final ExceptionType NOT_FOUND =
            new ExceptionType("error.notification.not_found", HttpStatus.NOT_FOUND, "NOTIFICATION_NOT_FOUND");
}
