package com.gucardev.springreactboilerplate.domain.organization.service.usecase;

import com.gucardev.springreactboilerplate.domain.organization.entity.Organization;
import com.gucardev.springreactboilerplate.domain.organization.exception.OrganizationExceptionType;
import com.gucardev.springreactboilerplate.domain.organization.mapper.OrganizationMapper;
import com.gucardev.springreactboilerplate.domain.organization.model.dto.OrganizationResponseDto;
import com.gucardev.springreactboilerplate.domain.organization.model.request.CreateOrganizationRequest;
import com.gucardev.springreactboilerplate.domain.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateOrganizationUseCase {

    private final OrganizationRepository repository;
    private final OrganizationMapper mapper;

    @Transactional
    public OrganizationResponseDto execute(CreateOrganizationRequest request) {
        if (repository.existsBySlug(request.slug())) {
            throw OrganizationExceptionType.SLUG_ALREADY_EXISTS.toException(request.slug());
        }
        Organization organization = mapper.toEntity(request);
        organization.setIsActive(request.isActive() == null || request.isActive());
        return mapper.toDto(repository.save(organization));
    }
}
