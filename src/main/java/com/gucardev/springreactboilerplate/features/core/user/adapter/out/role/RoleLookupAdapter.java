package com.gucardev.springreactboilerplate.features.core.user.adapter.out.role;

import com.gucardev.springreactboilerplate.features.core.role.adapter.out.persistence.RoleJpaEntity;
import com.gucardev.springreactboilerplate.features.core.role.adapter.out.persistence.RoleJpaRepository;
import com.gucardev.springreactboilerplate.features.core.role.domain.model.Role;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.RoleLookupPort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing {@link RoleLookupPort} by delegating to the role feature's repository
 * (preserving its cached {@code findByName}). This is the ONLY place in the user module — besides the
 * {@code UserJpaEntity}/{@code UserPersistenceMapper} association mapping — that depends on the role
 * persistence types, keeping the user core decoupled from the role internals.
 */
@Component
@RequiredArgsConstructor
public class RoleLookupAdapter implements RoleLookupPort {

    private final RoleJpaRepository roleJpaRepository;

    @Override
    public Optional<Role> findByName(String name) {
        return roleJpaRepository.findByName(name).map(this::toDomain);
    }

    private Role toDomain(RoleJpaEntity entity) {
        return Role.builder()
                .id(entity.getId())
                .name(entity.getName())
                .displayName(entity.getDisplayName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
