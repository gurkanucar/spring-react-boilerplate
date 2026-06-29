package com.gucardev.springreactboilerplate.features.core.notification.application.port.in;

/**
 * Input port: count the current user's unread notifications in the active workspace.
 */
public interface GetUnreadCountUseCase {

    long getUnreadCount();
}
