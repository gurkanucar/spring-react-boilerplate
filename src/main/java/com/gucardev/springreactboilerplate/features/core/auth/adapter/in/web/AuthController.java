package com.gucardev.springreactboilerplate.features.core.auth.adapter.in.web;

import com.gucardev.springreactboilerplate.features.core.auth.adapter.in.web.dto.LoginRequest;
import com.gucardev.springreactboilerplate.features.core.auth.adapter.in.web.dto.LogoutRequest;
import com.gucardev.springreactboilerplate.features.core.auth.adapter.in.web.dto.RefreshTokenRequest;
import com.gucardev.springreactboilerplate.features.core.auth.adapter.in.web.dto.RegisterRequest;
import com.gucardev.springreactboilerplate.features.core.auth.adapter.in.web.dto.TokenResponseDto;
import com.gucardev.springreactboilerplate.features.core.auth.application.port.in.GetCurrentUserUseCase;
import com.gucardev.springreactboilerplate.features.core.auth.application.port.in.LoginCommand;
import com.gucardev.springreactboilerplate.features.core.auth.application.port.in.LoginUseCase;
import com.gucardev.springreactboilerplate.features.core.auth.application.port.in.LogoutUseCase;
import com.gucardev.springreactboilerplate.features.core.auth.application.port.in.RefreshTokenUseCase;
import com.gucardev.springreactboilerplate.features.core.auth.application.port.in.RegisterCommand;
import com.gucardev.springreactboilerplate.features.core.auth.application.port.in.RegisterUseCase;
import com.gucardev.springreactboilerplate.features.core.user.adapter.in.web.UserWebMapper;
import com.gucardev.springreactboilerplate.features.core.user.adapter.in.web.dto.UserResponseDto;
import com.gucardev.springreactboilerplate.features.core.user.application.port.in.RemoveProfileImageUseCase;
import com.gucardev.springreactboilerplate.features.core.user.application.port.in.SetProfileImageCommand;
import com.gucardev.springreactboilerplate.features.core.user.application.port.in.SetProfileImageUseCase;
import com.gucardev.springreactboilerplate.features.core.user.domain.model.ProfileImageStorageType;
import com.gucardev.springreactboilerplate.infra.config.response.ApiResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Authentication endpoints. {@code register/login/refresh/logout} are public (bound in
 * {@code security.ignored-paths}); {@code /auth/me} and the profile-image endpoints require a valid
 * bearer token. The controller only talks to input ports and maps between web DTOs and the domain.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Registration, login and JWT token lifecycle.")
public class AuthController {

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final SetProfileImageUseCase setProfileImageUseCase;
    private final RemoveProfileImageUseCase removeProfileImageUseCase;
    private final AuthWebMapper authWebMapper;
    private final UserWebMapper userWebMapper;

    @Operation(summary = "Register a new account and receive tokens")
    @PostMapping("/register")
    public ResponseEntity<ApiResponseWrapper<TokenResponseDto>> register(
            @Valid @RequestBody RegisterRequest request) {
        TokenResponseDto response = authWebMapper.toResponse(registerUseCase.register(
                new RegisterCommand(request.email(), request.password(), request.name(),
                        request.surname(), request.phoneNumber())));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.created(response));
    }

    @Operation(summary = "Log in with email and password")
    @PostMapping("/login")
    public ResponseEntity<ApiResponseWrapper<TokenResponseDto>> login(
            @Valid @RequestBody LoginRequest request) {
        TokenResponseDto response = authWebMapper.toResponse(loginUseCase.login(
                new LoginCommand(request.email(), request.password())));
        return ResponseEntity.ok(ApiResponseWrapper.ok(response));
    }

    @Operation(summary = "Exchange a refresh token for a new (rotated) token bundle")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponseWrapper<TokenResponseDto>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        TokenResponseDto response = authWebMapper.toResponse(
                refreshTokenUseCase.refresh(request.refreshToken()));
        return ResponseEntity.ok(ApiResponseWrapper.ok(response));
    }

    @Operation(summary = "Revoke a refresh token")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseWrapper<Void>> logout(
            @Valid @RequestBody LogoutRequest request) {
        logoutUseCase.logout(request.refreshToken());
        return ResponseEntity.ok(ApiResponseWrapper.ok((Void) null, "Logged out"));
    }

    @Operation(summary = "Get the currently authenticated user")
    @GetMapping("/me")
    public ResponseEntity<ApiResponseWrapper<UserResponseDto>> me() {
        return ResponseEntity.ok(ApiResponseWrapper.ok(
                userWebMapper.toResponse(getCurrentUserUseCase.getCurrentUser())));
    }

    @Operation(summary = "Set the current user's profile image (optimized + thumbnailed). "
            + "storageType selects the backend; omit for the default.")
    @PostMapping(value = "/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseWrapper<UserResponseDto>> setProfileImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "storageType", required = false) ProfileImageStorageType storageType) {
        UserResponseDto response = userWebMapper.toResponse(setProfileImageUseCase.setProfileImage(
                new SetProfileImageCommand(currentUserEmail(), file, storageType)));
        return ResponseEntity.ok(ApiResponseWrapper.ok(response));
    }

    @Operation(summary = "Remove the current user's profile image")
    @DeleteMapping("/me/profile-image")
    public ResponseEntity<ApiResponseWrapper<UserResponseDto>> removeProfileImage() {
        UserResponseDto response = userWebMapper.toResponse(
                removeProfileImageUseCase.removeProfileImage(currentUserEmail()));
        return ResponseEntity.ok(ApiResponseWrapper.ok(response));
    }

    private String currentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
