package com.gucardev.springreactboilerplate.features.core.notification.application.port.in;

import com.gucardev.springreactboilerplate.features.core.notification.domain.model.Notification;
import org.springframework.data.domain.Page;

/**
 * Input port: list the current user's notifications in the active workspace (paged). Driving adapters
 * depend on this interface, not on the implementing service.
 */
public interface GetMyNotificationsUseCase {

    Page<Notification> getMine(NotificationQuery query);
}
