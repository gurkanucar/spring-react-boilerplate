package com.gucardev.springreactboilerplate.domain.user.service.usecase;

import com.gucardev.springreactboilerplate.domain.user.entity.User;
import com.gucardev.springreactboilerplate.domain.user.exception.UserExceptionType;
import com.gucardev.springreactboilerplate.domain.user.mapper.UserMapper;
import com.gucardev.springreactboilerplate.domain.user.model.dto.UserResponseDto;
import com.gucardev.springreactboilerplate.domain.user.model.request.CreateUserRequest;
import com.gucardev.springreactboilerplate.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Admin user creation: enforces email uniqueness, hashes the password and resolves the requested
 * roles (defaulting to {@code USER}). Status flags default to {@code true} when omitted.
 */
@Service
@RequiredArgsConstructor
public class CreateUserUseCase {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleResolver roleResolver;

    @Transactional
    public UserResponseDto execute(CreateUserRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw UserExceptionType.EMAIL_ALREADY_EXISTS.toException(request.email());
        }

        User user = mapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setActivated(request.activated() == null || request.activated());
        user.setIsActive(request.isActive() == null || request.isActive());
        user.setRoles(roleResolver.resolve(request.roles()));

        return mapper.toDto(repository.save(user));
    }
}
