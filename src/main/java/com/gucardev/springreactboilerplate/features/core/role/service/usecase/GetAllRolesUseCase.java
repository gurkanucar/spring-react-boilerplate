package com.gucardev.springreactboilerplate.features.core.role.service.usecase;

import com.gucardev.springreactboilerplate.features.core.role.mapper.RoleMapper;
import com.gucardev.springreactboilerplate.features.core.role.model.dto.RoleResponseDto;
import com.gucardev.springreactboilerplate.features.core.role.model.request.RoleFilterRequest;
import com.gucardev.springreactboilerplate.features.core.role.repository.RoleRepository;
import com.gucardev.springreactboilerplate.features.core.role.repository.specification.RoleSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAllRolesUseCase {

    private final RoleRepository repository;
    private final RoleMapper roleMapper;

    @Transactional(readOnly = true)
    public Page<RoleResponseDto> execute(RoleFilterRequest filter) {
        return repository.findAll(RoleSpecification.build(filter), filter.toPageable())
                .map(roleMapper::toDto);
    }
}
