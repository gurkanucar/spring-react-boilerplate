package com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.out;

import java.util.UUID;

/**
 * Output port: publish workspace lifecycle events so interested features can react within the current
 * transaction (e.g. seed feature-flag defaults on create, clean up workspace-scoped data on delete).
 * The driven adapter translates these to the shared-kernel events on the in-VM event bus.
 */
public interface WorkspaceEventPublisherPort {

    void publishCreated(UUID workspaceId);

    void publishDeleted(UUID workspaceId);
}
