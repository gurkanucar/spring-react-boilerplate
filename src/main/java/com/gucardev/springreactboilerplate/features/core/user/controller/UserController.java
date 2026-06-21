package com.gucardev.springreactboilerplate.features.core.user.controller;

import com.gucardev.springreactboilerplate.features.core.user.model.dto.UserResponseDto;
import com.gucardev.springreactboilerplate.features.core.user.model.request.CreateUserRequest;
import com.gucardev.springreactboilerplate.features.core.user.model.request.UpdateUserRequest;
import com.gucardev.springreactboilerplate.features.core.user.model.request.UserFilterRequest;
import com.gucardev.springreactboilerplate.features.core.user.service.usecase.CreateUserUseCase;
import com.gucardev.springreactboilerplate.features.core.user.service.usecase.DeleteUserUseCase;
import com.gucardev.springreactboilerplate.features.core.user.service.usecase.GetAllUsersUseCase;
import com.gucardev.springreactboilerplate.features.core.user.service.usecase.GetUserByIdUseCase;
import com.gucardev.springreactboilerplate.features.core.user.service.usecase.UpdateUserUseCase;
import com.gucardev.springreactboilerplate.infra.config.response.ApiResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin CRUD over users. One injected use case per operation, responses wrapped in the standard
 * {@link ApiResponseWrapper}. Self-service registration lives in {@code AuthController}.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "Administer user accounts.")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;

    @Operation(summary = "List users (paged, sorted and filtered)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<List<UserResponseDto>>> getAll(
            @Valid UserFilterRequest filter) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(getAllUsersUseCase.execute(filter)));
    }

    @Operation(summary = "Get a user by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<UserResponseDto>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(getUserByIdUseCase.execute(id)));
    }

    @Operation(summary = "Create a user")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<UserResponseDto>> create(
            @Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.created(createUserUseCase.execute(request)));
    }

    @Operation(summary = "Update a user")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<UserResponseDto>> update(
            @PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(updateUserUseCase.execute(id, request)));
    }

    @Operation(summary = "Delete a user")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<Void>> delete(@PathVariable UUID id) {
        deleteUserUseCase.execute(id);
        return ResponseEntity.ok(ApiResponseWrapper.ok((Void) null, "User deleted"));
    }
}
