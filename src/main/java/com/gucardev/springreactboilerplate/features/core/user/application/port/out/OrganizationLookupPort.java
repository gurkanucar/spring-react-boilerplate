package com.gucardev.springreactboilerplate.features.core.user.application.port.out;

import java.util.UUID;

/**
 * Output port: check whether an organization exists. Backed by a driven adapter that delegates to
 * the organization feature, keeping the user core off the organization internals.
 */
public interface OrganizationLookupPort {

    boolean existsById(UUID organizationId);
}
