package com.gucardev.springreactboilerplate.features.core.user.application.service;

import com.gucardev.springreactboilerplate.features.core.role.domain.model.Role;
import com.gucardev.springreactboilerplate.features.core.user.application.exception.UserExceptionType;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.RoleLookupPort;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Resolves a set of role names into domain {@link Role} models, used by the create/update user use
 * cases. An unknown name is rejected; an empty/omitted set falls back to the default role.
 */
@Service
@RequiredArgsConstructor
public class UserRoleResolver {

    private static final String DEFAULT_ROLE = "USER";

    private final RoleLookupPort roleLookupPort;

    public Set<Role> resolve(Set<String> names) {
        if (names == null || names.isEmpty()) {
            return new HashSet<>(Set.of(findByName(DEFAULT_ROLE)));
        }
        return names.stream().map(this::findByName).collect(Collectors.toSet());
    }

    private Role findByName(String name) {
        return roleLookupPort.findByName(name)
                .orElseThrow(() -> UserExceptionType.ROLE_NOT_FOUND.toException(name));
    }
}
