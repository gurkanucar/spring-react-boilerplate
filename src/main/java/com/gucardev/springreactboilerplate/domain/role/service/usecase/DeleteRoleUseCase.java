package com.gucardev.springreactboilerplate.domain.role.service.usecase;

import com.gucardev.springreactboilerplate.domain.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteRoleUseCase {

    private final RoleFinder finder;
    private final RoleRepository repository;

    @Transactional
    public void execute(Long id) {
        repository.delete(finder.findById(id));
    }
}
