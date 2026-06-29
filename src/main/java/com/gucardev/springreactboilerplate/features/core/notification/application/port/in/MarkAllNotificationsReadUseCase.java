package com.gucardev.springreactboilerplate.features.core.notification.application.port.in;

/**
 * Input port: mark all of the current user's unread notifications read; returns how many were updated.
 */
public interface MarkAllNotificationsReadUseCase {

    int markAllRead();
}
