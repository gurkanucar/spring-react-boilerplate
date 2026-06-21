package com.gucardev.springreactboilerplate.features.core.role.service.usecase;

import com.gucardev.springreactboilerplate.features.core.role.mapper.RoleMapper;
import com.gucardev.springreactboilerplate.features.core.role.model.dto.RoleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetRoleByIdUseCase {

    private final RoleFinder finder;
    private final RoleMapper roleMapper;

    @Transactional(readOnly = true)
    public RoleResponseDto execute(Long id) {
        return roleMapper.toDto(finder.findById(id));
    }
}
