package com.gucardev.springreactboilerplate.domain.auth.service.usecase;

import com.gucardev.springreactboilerplate.domain.auth.exception.AuthExceptionType;
import com.gucardev.springreactboilerplate.domain.auth.model.dto.TokenResponseDto;
import com.gucardev.springreactboilerplate.domain.auth.model.request.LoginRequest;
import com.gucardev.springreactboilerplate.domain.auth.service.AuthTokenService;
import com.gucardev.springreactboilerplate.domain.user.entity.User;
import com.gucardev.springreactboilerplate.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authenticates email + password through Spring's {@link AuthenticationManager} (which raises
 * {@code BadCredentialsException} / {@code DisabledException}, both translated centrally by
 * {@code GlobalExceptionHandler}) and then issues a token bundle.
 */
@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final AuthTokenService authTokenService;

    @Transactional
    public TokenResponseDto execute(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> AuthExceptionType.USER_NOT_FOUND.toException());

        return authTokenService.issueTokens(user);
    }
}
