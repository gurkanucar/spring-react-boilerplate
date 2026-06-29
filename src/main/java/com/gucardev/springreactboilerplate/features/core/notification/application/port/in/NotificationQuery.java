package com.gucardev.springreactboilerplate.features.core.notification.application.port.in;

import org.springframework.data.domain.Pageable;

/**
 * Driving-side query for listing the current user's notifications. Carries already-validated paging
 * input from a driving adapter into the application core, decoupling the core from the web filter DTO.
 */
public record NotificationQuery(
        Pageable pageable,
        Boolean unreadOnly
) {
}
