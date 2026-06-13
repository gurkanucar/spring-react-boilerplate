package com.gucardev.springreactboilerplate.domain.role.service.usecase;

import com.gucardev.springreactboilerplate.domain.role.mapper.RoleMapper;
import com.gucardev.springreactboilerplate.domain.role.model.dto.RoleResponseDto;
import com.gucardev.springreactboilerplate.domain.role.model.request.RoleFilterRequest;
import com.gucardev.springreactboilerplate.domain.role.repository.RoleRepository;
import com.gucardev.springreactboilerplate.domain.role.repository.specification.RoleSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAllRolesUseCase {

    private final RoleRepository repository;
    private final RoleMapper mapper;

    @Transactional(readOnly = true)
    public Page<RoleResponseDto> execute(RoleFilterRequest filter) {
        return repository.findAll(RoleSpecification.build(filter), filter.toPageable())
                .map(mapper::toDto);
    }
}
