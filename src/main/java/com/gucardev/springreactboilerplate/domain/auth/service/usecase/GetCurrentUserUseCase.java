package com.gucardev.springreactboilerplate.domain.auth.service.usecase;

import com.gucardev.springreactboilerplate.domain.auth.exception.AuthExceptionType;
import com.gucardev.springreactboilerplate.domain.user.mapper.UserMapper;
import com.gucardev.springreactboilerplate.domain.user.model.dto.UserResponseDto;
import com.gucardev.springreactboilerplate.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Returns the profile of the currently authenticated user, resolved from the security context
 * (the principal's username is the email set by the JWT filter).
 */
@Service
@RequiredArgsConstructor
public class GetCurrentUserUseCase {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserResponseDto execute() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw AuthExceptionType.USER_NOT_FOUND.toException();
        }
        return userRepository.findByEmail(authentication.getName())
                .map(userMapper::toDto)
                .orElseThrow(() -> AuthExceptionType.USER_NOT_FOUND.toException());
    }
}
