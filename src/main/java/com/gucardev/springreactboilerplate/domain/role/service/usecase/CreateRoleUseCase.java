package com.gucardev.springreactboilerplate.domain.role.service.usecase;

import com.gucardev.springreactboilerplate.domain.role.exception.RoleExceptionType;
import com.gucardev.springreactboilerplate.domain.role.mapper.RoleMapper;
import com.gucardev.springreactboilerplate.domain.role.model.dto.RoleResponseDto;
import com.gucardev.springreactboilerplate.domain.role.model.request.CreateRoleRequest;
import com.gucardev.springreactboilerplate.domain.role.repository.RoleRepository;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheManagers;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheNames;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateRoleUseCase {

    private final RoleRepository repository;
    private final RoleMapper mapper;

    @CacheEvict(cacheNames = CacheNames.ROLES, cacheManager = CacheManagers.CAFFEINE_30M, allEntries = true)
    @Transactional
    public RoleResponseDto execute(CreateRoleRequest request) {
        if (repository.existsByName(request.name())) {
            throw RoleExceptionType.NAME_ALREADY_EXISTS.toException(request.name());
        }
        return mapper.toDto(repository.save(mapper.toEntity(request)));
    }
}
