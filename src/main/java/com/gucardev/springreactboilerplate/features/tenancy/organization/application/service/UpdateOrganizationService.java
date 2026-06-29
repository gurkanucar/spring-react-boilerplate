package com.gucardev.springreactboilerplate.features.tenancy.organization.application.service;

import com.gucardev.springreactboilerplate.features.tenancy.organization.application.exception.OrganizationExceptionType;
import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.in.UpdateOrganizationCommand;
import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.in.UpdateOrganizationUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.out.LoadOrganizationPort;
import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.out.SaveOrganizationPort;
import com.gucardev.springreactboilerplate.features.tenancy.organization.domain.model.Organization;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateOrganizationService implements UpdateOrganizationUseCase {

    private final OrganizationFinder finder;
    private final LoadOrganizationPort loadOrganizationPort;
    private final SaveOrganizationPort saveOrganizationPort;

    @Override
    @Transactional
    public Organization update(UUID id, UpdateOrganizationCommand command) {
        Organization organization = finder.findById(id);
        if (command.slug() != null && !command.slug().equals(organization.getSlug())
                && loadOrganizationPort.existsBySlug(command.slug())) {
            throw OrganizationExceptionType.SLUG_ALREADY_EXISTS.toException(command.slug());
        }
        applyNonNull(command, organization);
        return saveOrganizationPort.save(organization);
    }

    /** Patch only non-null command fields onto the organization (null fields are left unchanged). */
    private void applyNonNull(UpdateOrganizationCommand command, Organization organization) {
        if (command.name() != null) {
            organization.setName(command.name());
        }
        if (command.slug() != null) {
            organization.setSlug(command.slug());
        }
        if (command.description() != null) {
            organization.setDescription(command.description());
        }
        if (command.phoneNumber() != null) {
            organization.setPhoneNumber(command.phoneNumber());
        }
        if (command.address() != null) {
            organization.setAddress(command.address());
        }
        if (command.isActive() != null) {
            organization.setIsActive(command.isActive());
        }
        if (command.logoId() != null) {
            organization.setLogoId(command.logoId());
        }
    }
}
