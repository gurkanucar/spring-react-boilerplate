package com.gucardev.springreactboilerplate.features.tenancy.organization.application.service;

import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.in.GetOrganizationUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.out.LoadOrganizationPort;
import com.gucardev.springreactboilerplate.features.tenancy.organization.domain.model.Organization;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetOrganizationService implements GetOrganizationUseCase {

    private final OrganizationFinder finder;
    private final LoadOrganizationPort loadOrganizationPort;

    @Override
    @Transactional(readOnly = true)
    public Organization getById(UUID id) {
        return finder.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        return loadOrganizationPort.existsById(id);
    }
}
