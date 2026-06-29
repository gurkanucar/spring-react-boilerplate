package com.gucardev.springreactboilerplate.features.core.role.application.service;

import com.gucardev.springreactboilerplate.features.core.role.application.port.in.GetRoleByIdUseCase;
import com.gucardev.springreactboilerplate.features.core.role.domain.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetRoleByIdService implements GetRoleByIdUseCase {

    private final RoleFinder finder;

    @Override
    @Transactional(readOnly = true)
    public Role getById(Long id) {
        return finder.findById(id);
    }
}
