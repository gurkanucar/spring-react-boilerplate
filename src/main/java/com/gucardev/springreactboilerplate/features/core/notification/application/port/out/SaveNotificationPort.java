package com.gucardev.springreactboilerplate.features.core.notification.application.port.out;

import com.gucardev.springreactboilerplate.features.core.notification.domain.model.Notification;

/**
 * Output port: persist a notification (insert or update) and return the stored state, including any
 * generated id and audit metadata.
 */
public interface SaveNotificationPort {

    Notification save(Notification notification);
}
