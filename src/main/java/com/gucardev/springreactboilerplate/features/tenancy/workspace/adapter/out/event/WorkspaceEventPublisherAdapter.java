package com.gucardev.springreactboilerplate.features.tenancy.workspace.adapter.out.event;

import com.gucardev.springreactboilerplate.features.shared.event.WorkspaceCreatedEvent;
import com.gucardev.springreactboilerplate.features.shared.event.WorkspaceDeletedEvent;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.out.WorkspaceEventPublisherPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing {@link WorkspaceEventPublisherPort} with Spring's in-VM event bus. Publishes
 * the shared-kernel workspace lifecycle events; handlers run synchronously within the caller's
 * transaction.
 */
@Component
@RequiredArgsConstructor
public class WorkspaceEventPublisherAdapter implements WorkspaceEventPublisherPort {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publishCreated(UUID workspaceId) {
        eventPublisher.publishEvent(new WorkspaceCreatedEvent(workspaceId));
    }

    @Override
    public void publishDeleted(UUID workspaceId) {
        eventPublisher.publishEvent(new WorkspaceDeletedEvent(workspaceId));
    }
}
