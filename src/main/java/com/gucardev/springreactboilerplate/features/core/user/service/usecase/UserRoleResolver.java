package com.gucardev.springreactboilerplate.features.core.user.service.usecase;

import com.gucardev.springreactboilerplate.features.core.role.entity.Role;
import com.gucardev.springreactboilerplate.features.core.role.repository.RoleRepository;
import com.gucardev.springreactboilerplate.features.core.user.exception.UserExceptionType;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Resolves a set of role names into managed {@link Role} entities, used by the create/update user
 * use cases. An unknown name is rejected; an empty/omitted set falls back to the default role.
 */
@Service
@RequiredArgsConstructor
public class UserRoleResolver {

    private static final String DEFAULT_ROLE = "USER";

    private final RoleRepository roleRepository;

    public Set<Role> resolve(Set<String> names) {
        if (names == null || names.isEmpty()) {
            return new HashSet<>(Set.of(findByName(DEFAULT_ROLE)));
        }
        return names.stream().map(this::findByName).collect(Collectors.toSet());
    }

    private Role findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> UserExceptionType.ROLE_NOT_FOUND.toException(name));
    }
}
