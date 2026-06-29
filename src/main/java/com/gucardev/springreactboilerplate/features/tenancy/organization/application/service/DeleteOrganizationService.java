package com.gucardev.springreactboilerplate.features.tenancy.organization.application.service;

import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.in.DeleteOrganizationUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.out.DeleteOrganizationPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteOrganizationService implements DeleteOrganizationUseCase {

    private final OrganizationFinder finder;
    private final DeleteOrganizationPort deleteOrganizationPort;

    @Override
    @Transactional
    public void delete(UUID id) {
        deleteOrganizationPort.delete(finder.findById(id));
    }
}
