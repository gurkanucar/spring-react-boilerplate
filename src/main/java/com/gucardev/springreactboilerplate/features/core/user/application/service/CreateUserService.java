package com.gucardev.springreactboilerplate.features.core.user.application.service;

import com.gucardev.springreactboilerplate.features.core.user.application.exception.UserExceptionType;
import com.gucardev.springreactboilerplate.features.core.user.application.port.in.CreateUserCommand;
import com.gucardev.springreactboilerplate.features.core.user.application.port.in.CreateUserUseCase;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.LoadUserPort;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.PasswordEncoderPort;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.SaveUserPort;
import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Admin user creation: enforces email uniqueness, hashes the password and resolves the requested
 * roles (defaulting to {@code USER}). Status flags default to {@code true} when omitted.
 */
@Service
@RequiredArgsConstructor
public class CreateUserService implements CreateUserUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final UserRoleResolver roleResolver;
    private final UserTenantAssignmentValidator tenantAssignmentValidator;

    @Override
    @Transactional
    public User create(CreateUserCommand command) {
        if (loadUserPort.existsByEmail(command.email())) {
            throw UserExceptionType.EMAIL_ALREADY_EXISTS.toException(command.email());
        }

        User user = User.builder()
                .email(command.email())
                .password(passwordEncoderPort.encode(command.password()))
                .name(command.name())
                .surname(command.surname())
                .phoneNumber(command.phoneNumber())
                .organizationId(command.organizationId())
                .workspaceId(command.workspaceId())
                .activated(command.activated() == null || command.activated())
                .isActive(command.isActive() == null || command.isActive())
                .roles(roleResolver.resolve(command.roles()))
                .build();
        tenantAssignmentValidator.validate(user.getOrganizationId(), user.getWorkspaceId());

        return saveUserPort.save(user);
    }
}
