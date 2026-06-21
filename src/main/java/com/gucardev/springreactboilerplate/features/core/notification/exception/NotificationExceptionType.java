package com.gucardev.springreactboilerplate.features.core.notification.exception;

import com.gucardev.springreactboilerplate.infra.exception.model.ExceptionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotificationExceptionType {

    public static final ExceptionType NOT_FOUND =
            new ExceptionType("error.notification.not_found", HttpStatus.NOT_FOUND, "NOTIFICATION_NOT_FOUND");
}
