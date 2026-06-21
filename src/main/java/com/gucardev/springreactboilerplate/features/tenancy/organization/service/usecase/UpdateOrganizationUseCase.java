package com.gucardev.springreactboilerplate.features.tenancy.organization.service.usecase;

import com.gucardev.springreactboilerplate.features.tenancy.organization.entity.Organization;
import com.gucardev.springreactboilerplate.features.tenancy.organization.exception.OrganizationExceptionType;
import com.gucardev.springreactboilerplate.features.tenancy.organization.mapper.OrganizationMapper;
import com.gucardev.springreactboilerplate.features.tenancy.organization.model.dto.OrganizationResponseDto;
import com.gucardev.springreactboilerplate.features.tenancy.organization.model.request.UpdateOrganizationRequest;
import com.gucardev.springreactboilerplate.features.tenancy.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateOrganizationUseCase {

    private final OrganizationFinder finder;
    private final OrganizationRepository repository;
    private final OrganizationMapper mapper;

    @Transactional
    public OrganizationResponseDto execute(java.util.UUID id, UpdateOrganizationRequest request) {
        Organization organization = finder.findById(id);
        if (request.slug() != null && !request.slug().equals(organization.getSlug())
                && repository.existsBySlug(request.slug())) {
            throw OrganizationExceptionType.SLUG_ALREADY_EXISTS.toException(request.slug());
        }
        mapper.updateEntity(request, organization);
        return mapper.toDto(repository.save(organization));
    }
}
