package com.gucardev.springreactboilerplate.features.tenancy.organization.application.service;

import com.gucardev.springreactboilerplate.features.tenancy.organization.application.exception.OrganizationExceptionType;
import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.out.LoadOrganizationPort;
import com.gucardev.springreactboilerplate.features.tenancy.organization.domain.model.Organization;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Shared "fetch or 404" lookup for organizations, used by the use-case services.
 */
@Service
@RequiredArgsConstructor
public class OrganizationFinder {

    private final LoadOrganizationPort loadOrganizationPort;

    public Organization findById(UUID id) {
        return loadOrganizationPort.findById(id)
                .orElseThrow(() -> OrganizationExceptionType.NOT_FOUND.toException(id));
    }
}
