package com.gucardev.springreactboilerplate.features.core.notification.application.port.in;

/**
 * Input port: create an in-app notification (subject to the per-workspace feature flag). Invoked by
 * driving adapters such as the domain-event listener.
 */
public interface CreateNotificationUseCase {

    void create(CreateNotificationCommand command);
}
