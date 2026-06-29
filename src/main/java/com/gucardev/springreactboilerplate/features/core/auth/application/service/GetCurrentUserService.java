package com.gucardev.springreactboilerplate.features.core.auth.application.service;

import com.gucardev.springreactboilerplate.features.core.auth.application.exception.AuthExceptionType;
import com.gucardev.springreactboilerplate.features.core.auth.application.port.in.GetCurrentUserUseCase;
import com.gucardev.springreactboilerplate.features.core.auth.application.port.out.LoadUserPort;
import com.gucardev.springreactboilerplate.features.core.user.domain.model.User;
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
public class GetCurrentUserService implements GetCurrentUserUseCase {

    private final LoadUserPort loadUserPort;

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw AuthExceptionType.USER_NOT_FOUND.toException();
        }
        return loadUserPort.findByEmail(authentication.getName())
                .orElseThrow(AuthExceptionType.USER_NOT_FOUND::toException);
    }
}
