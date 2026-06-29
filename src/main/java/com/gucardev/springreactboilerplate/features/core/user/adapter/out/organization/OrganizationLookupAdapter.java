package com.gucardev.springreactboilerplate.features.core.user.adapter.out.organization;

import com.gucardev.springreactboilerplate.features.core.user.application.port.out.OrganizationLookupPort;
import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.in.GetOrganizationUseCase;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing {@link OrganizationLookupPort} by delegating to the organization feature's
 * input port. This is the ONLY place in the user module that depends on organization types.
 */
@Component
@RequiredArgsConstructor
public class OrganizationLookupAdapter implements OrganizationLookupPort {

    private final GetOrganizationUseCase getOrganizationUseCase;

    @Override
    public boolean existsById(UUID organizationId) {
        return getOrganizationUseCase.existsById(organizationId);
    }
}
