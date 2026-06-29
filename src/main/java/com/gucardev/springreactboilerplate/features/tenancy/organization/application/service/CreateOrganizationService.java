package com.gucardev.springreactboilerplate.features.tenancy.organization.application.service;

import com.gucardev.springreactboilerplate.features.tenancy.organization.application.exception.OrganizationExceptionType;
import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.in.CreateOrganizationCommand;
import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.in.CreateOrganizationUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.out.LoadOrganizationPort;
import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.out.SaveOrganizationPort;
import com.gucardev.springreactboilerplate.features.tenancy.organization.domain.model.Organization;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateOrganizationService implements CreateOrganizationUseCase {

    private final LoadOrganizationPort loadOrganizationPort;
    private final SaveOrganizationPort saveOrganizationPort;

    @Override
    @Transactional
    public Organization create(CreateOrganizationCommand command) {
        if (loadOrganizationPort.existsBySlug(command.slug())) {
            throw OrganizationExceptionType.SLUG_ALREADY_EXISTS.toException(command.slug());
        }
        Organization organization = Organization.builder()
                .name(command.name())
                .slug(command.slug())
                .description(command.description())
                .phoneNumber(command.phoneNumber())
                .address(command.address())
                .isActive(command.isActive() == null || command.isActive())
                .logoId(command.logoId())
                .build();
        return saveOrganizationPort.save(organization);
    }
}
