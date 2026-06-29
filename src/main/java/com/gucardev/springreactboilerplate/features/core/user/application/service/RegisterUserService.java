package com.gucardev.springreactboilerplate.features.core.user.application.service;

import com.gucardev.springreactboilerplate.features.core.role.domain.model.Role;
import com.gucardev.springreactboilerplate.features.core.user.application.exception.UserExceptionType;
import com.gucardev.springreactboilerplate.features.core.user.application.port.in.RegisterUserCommand;
import com.gucardev.springreactboilerplate.features.core.user.application.port.in.RegisterUserUseCase;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.LoadUserPort;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.PasswordEncoderPort;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.RoleLookupPort;
import com.gucardev.springreactboilerplate.features.core.user.application.port.out.SaveUserPort;
import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Self-registration: enforces email uniqueness, hashes the password and grants the default
 * {@code USER} role. Returns the created domain user; the auth feature issues tokens from it.
 */
@Service
@RequiredArgsConstructor
public class RegisterUserService implements RegisterUserUseCase {

    private static final String DEFAULT_ROLE = "USER";

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final RoleLookupPort roleLookupPort;
    private final PasswordEncoderPort passwordEncoderPort;

    @Override
    @Transactional
    public User register(RegisterUserCommand command) {
        if (loadUserPort.existsByEmail(command.email())) {
            throw UserExceptionType.EMAIL_ALREADY_EXISTS.toException(command.email());
        }

        Role userRole = roleLookupPort.findByName(DEFAULT_ROLE)
                .orElseThrow(() -> UserExceptionType.DEFAULT_ROLE_NOT_FOUND.toException(DEFAULT_ROLE));

        User user = User.builder()
                .email(command.email())
                .password(passwordEncoderPort.encode(command.password()))
                .name(command.name())
                .surname(command.surname())
                .phoneNumber(command.phoneNumber())
                .activated(true)
                .isActive(true)
                .build();
        user.addRole(userRole);

        return saveUserPort.save(user);
    }
}
