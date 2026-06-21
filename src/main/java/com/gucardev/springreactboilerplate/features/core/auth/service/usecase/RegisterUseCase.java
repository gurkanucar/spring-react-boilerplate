package com.gucardev.springreactboilerplate.features.core.auth.service.usecase;

import com.gucardev.springreactboilerplate.features.core.auth.exception.AuthExceptionType;
import com.gucardev.springreactboilerplate.features.core.auth.model.dto.TokenResponseDto;
import com.gucardev.springreactboilerplate.features.core.auth.model.request.RegisterRequest;
import com.gucardev.springreactboilerplate.features.core.auth.service.AuthTokenService;
import com.gucardev.springreactboilerplate.features.core.role.entity.Role;
import com.gucardev.springreactboilerplate.features.core.role.repository.RoleRepository;
import com.gucardev.springreactboilerplate.features.core.user.entity.User;
import com.gucardev.springreactboilerplate.features.core.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Registers a new account: enforces email uniqueness, hashes the password, grants the default
 * {@code USER} role and immediately issues tokens (auto-login).
 */
@Service
@RequiredArgsConstructor
public class RegisterUseCase {

    private static final String DEFAULT_ROLE = "USER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService authTokenService;

    @Transactional
    public TokenResponseDto execute(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw AuthExceptionType.EMAIL_ALREADY_EXISTS.toException(request.email());
        }

        Role userRole = roleRepository.findByName(DEFAULT_ROLE)
                .orElseThrow(() -> AuthExceptionType.ROLE_NOT_FOUND.toException(DEFAULT_ROLE));

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .surname(request.surname())
                .phoneNumber(request.phoneNumber())
                .activated(true)
                .isActive(true)
                .build();
        user.addRole(userRole);

        User saved = userRepository.save(user);
        return authTokenService.issueTokens(saved);
    }
}
