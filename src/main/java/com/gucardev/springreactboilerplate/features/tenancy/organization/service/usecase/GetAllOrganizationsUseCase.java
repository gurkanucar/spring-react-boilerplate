package com.gucardev.springreactboilerplate.features.tenancy.organization.service.usecase;

import com.gucardev.springreactboilerplate.features.tenancy.organization.mapper.OrganizationMapper;
import com.gucardev.springreactboilerplate.features.tenancy.organization.model.dto.OrganizationResponseDto;
import com.gucardev.springreactboilerplate.features.tenancy.organization.model.request.OrganizationFilterRequest;
import com.gucardev.springreactboilerplate.features.tenancy.organization.repository.OrganizationRepository;
import com.gucardev.springreactboilerplate.features.tenancy.organization.repository.specification.OrganizationSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAllOrganizationsUseCase {

    private final OrganizationRepository repository;
    private final OrganizationMapper mapper;

    @Transactional(readOnly = true)
    public Page<OrganizationResponseDto> execute(OrganizationFilterRequest filter) {
        return repository.findAll(OrganizationSpecification.build(filter), filter.toPageable())
                .map(mapper::toDto);
    }
}
