package com.gucardev.springreactboilerplate.features.scheduledevent.exception;

import com.gucardev.springreactboilerplate.infra.exception.model.ExceptionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Domain error catalog for scheduled events. Throw via a constant, e.g.
 * {@code throw ScheduledEventExceptionType.NOT_FOUND.toException(id);}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScheduledEventExceptionType {

    public static final ExceptionType NOT_FOUND =
            new ExceptionType("error.scheduled_event.not_found", HttpStatus.NOT_FOUND, "SCHEDULED_EVENT_NOT_FOUND");

    /** Only a still-pending (SCHEDULED) event can be cancelled. */
    public static final ExceptionType NOT_CANCELLABLE =
            new ExceptionType("error.scheduled_event.not_cancellable", HttpStatus.CONFLICT, "SCHEDULED_EVENT_NOT_CANCELLABLE");
}
