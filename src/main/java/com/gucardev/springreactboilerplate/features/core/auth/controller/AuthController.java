package com.gucardev.springreactboilerplate.features.core.auth.controller;

import com.gucardev.springreactboilerplate.features.core.auth.model.dto.TokenResponseDto;
import com.gucardev.springreactboilerplate.features.core.auth.model.request.LoginRequest;
import com.gucardev.springreactboilerplate.features.core.auth.model.request.LogoutRequest;
import com.gucardev.springreactboilerplate.features.core.auth.model.request.RefreshTokenRequest;
import com.gucardev.springreactboilerplate.features.core.auth.model.request.RegisterRequest;
import com.gucardev.springreactboilerplate.features.core.auth.service.usecase.GetCurrentUserUseCase;
import com.gucardev.springreactboilerplate.features.core.auth.service.usecase.LoginUseCase;
import com.gucardev.springreactboilerplate.features.core.auth.service.usecase.LogoutUseCase;
import com.gucardev.springreactboilerplate.features.core.auth.service.usecase.RefreshTokenUseCase;
import com.gucardev.springreactboilerplate.features.core.auth.service.usecase.RegisterUseCase;
import com.gucardev.springreactboilerplate.features.core.file.enums.StorageType;
import com.gucardev.springreactboilerplate.features.core.user.model.dto.UserResponseDto;
import com.gucardev.springreactboilerplate.features.core.user.service.usecase.RemoveProfileImageUseCase;
import com.gucardev.springreactboilerplate.features.core.user.service.usecase.SetProfileImageUseCase;
import com.gucardev.springreactboilerplate.infra.config.response.ApiResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
 * {@code security.ignored-paths}); {@code /auth/me} requires a valid bearer token.
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

    @Operation(summary = "Register a new account and receive tokens")
    @PostMapping("/register")
    public ResponseEntity<ApiResponseWrapper<TokenResponseDto>> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.created(registerUseCase.execute(request)));
    }

    @Operation(summary = "Log in with email and password")
    @PostMapping("/login")
    public ResponseEntity<ApiResponseWrapper<TokenResponseDto>> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(loginUseCase.execute(request)));
    }

    @Operation(summary = "Exchange a refresh token for a new (rotated) token bundle")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponseWrapper<TokenResponseDto>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(refreshTokenUseCase.execute(request)));
    }

    @Operation(summary = "Revoke a refresh token")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseWrapper<Void>> logout(
            @Valid @RequestBody LogoutRequest request) {
        logoutUseCase.execute(request.refreshToken());
        return ResponseEntity.ok(ApiResponseWrapper.ok((Void) null, "Logged out"));
    }

    @Operation(summary = "Get the currently authenticated user")
    @GetMapping("/me")
    public ResponseEntity<ApiResponseWrapper<UserResponseDto>> me() {
        return ResponseEntity.ok(ApiResponseWrapper.ok(getCurrentUserUseCase.execute()));
    }

    @Operation(summary = "Set the current user's profile image (optimized + thumbnailed). "
            + "storageType selects the backend; omit for the default.")
    @PostMapping(value = "/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseWrapper<UserResponseDto>> setProfileImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "storageType", required = false) StorageType storageType) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(setProfileImageUseCase.execute(file, storageType)));
    }

    @Operation(summary = "Remove the current user's profile image")
    @DeleteMapping("/me/profile-image")
    public ResponseEntity<ApiResponseWrapper<UserResponseDto>> removeProfileImage() {
        return ResponseEntity.ok(ApiResponseWrapper.ok(removeProfileImageUseCase.execute()));
    }
}
