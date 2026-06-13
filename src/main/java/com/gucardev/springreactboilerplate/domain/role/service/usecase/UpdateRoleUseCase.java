package com.gucardev.springreactboilerplate.domain.role.service.usecase;

import com.gucardev.springreactboilerplate.domain.role.entity.Role;
import com.gucardev.springreactboilerplate.domain.role.mapper.RoleMapper;
import com.gucardev.springreactboilerplate.domain.role.model.dto.RoleResponseDto;
import com.gucardev.springreactboilerplate.domain.role.model.request.UpdateRoleRequest;
import com.gucardev.springreactboilerplate.domain.role.repository.RoleRepository;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheManagers;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheNames;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateRoleUseCase {

    private final RoleFinder finder;
    private final RoleRepository repository;
    private final RoleMapper mapper;

    @CacheEvict(cacheNames = CacheNames.ROLES, cacheManager = CacheManagers.CAFFEINE_30M, allEntries = true)
    @Transactional
    public RoleResponseDto execute(Long id, UpdateRoleRequest request) {
        Role role = finder.findById(id);
        mapper.updateEntity(request, role);
        return mapper.toDto(repository.save(role));
    }
}
