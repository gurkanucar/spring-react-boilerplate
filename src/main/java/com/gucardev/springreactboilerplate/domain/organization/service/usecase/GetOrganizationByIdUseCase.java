package com.gucardev.springreactboilerplate.domain.organization.service.usecase;

import com.gucardev.springreactboilerplate.domain.organization.mapper.OrganizationMapper;
import com.gucardev.springreactboilerplate.domain.organization.model.dto.OrganizationResponseDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetOrganizationByIdUseCase {

    private final OrganizationFinder finder;
    private final OrganizationMapper mapper;

    @Transactional(readOnly = true)
    public OrganizationResponseDto execute(UUID id) {
        return mapper.toDto(finder.findById(id));
    }
}
