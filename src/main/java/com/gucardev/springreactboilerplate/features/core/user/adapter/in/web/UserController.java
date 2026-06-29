package com.gucardev.springreactboilerplate.features.core.user.adapter.in.web;

import com.gucardev.springreactboilerplate.features.core.user.adapter.in.web.dto.CreateUserRequest;
import com.gucardev.springreactboilerplate.features.core.user.adapter.in.web.dto.UpdateUserRequest;
import com.gucardev.springreactboilerplate.features.core.user.adapter.in.web.dto.UserFilterRequest;
import com.gucardev.springreactboilerplate.features.core.user.adapter.in.web.dto.UserResponseDto;
import com.gucardev.springreactboilerplate.features.core.user.application.port.in.CreateUserCommand;
import com.gucardev.springreactboilerplate.features.core.user.application.port.in.CreateUserUseCase;
import com.gucardev.springreactboilerplate.features.core.user.application.port.in.DeleteUserUseCase;
import com.gucardev.springreactboilerplate.features.core.user.application.port.in.GetAllUsersUseCase;
import com.gucardev.springreactboilerplate.features.core.user.application.port.in.GetUserByIdUseCase;
import com.gucardev.springreactboilerplate.features.core.user.application.port.in.UpdateUserCommand;
import com.gucardev.springreactboilerplate.features.core.user.application.port.in.UpdateUserUseCase;
import com.gucardev.springreactboilerplate.features.core.user.application.port.in.UserSearchQuery;
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
 * Admin CRUD over users. One injected input port per operation, responses wrapped in the standard
 * {@link ApiResponseWrapper}. The controller only talks to input ports and maps between web DTOs and
 * the domain model. Self-service registration lives in {@code AuthController}.
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
    private final UserWebMapper userWebMapper;

    @Operation(summary = "List users (paged, sorted and filtered)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<List<UserResponseDto>>> getAll(
            @Valid UserFilterRequest filter) {
        UserSearchQuery query = new UserSearchQuery(
                filter.getEmail(), filter.getName(), filter.getActivated(), filter.getIsActive(),
                filter.getStartDate(), filter.getEndDate(), filter.toPageable());
        return ResponseEntity.ok(ApiResponseWrapper.ok(
                getAllUsersUseCase.getAll(query).map(userWebMapper::toResponse)));
    }

    @Operation(summary = "Get a user by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<UserResponseDto>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(
                userWebMapper.toResponse(getUserByIdUseCase.getById(id))));
    }

    @Operation(summary = "Create a user")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<UserResponseDto>> create(
            @Valid @RequestBody CreateUserRequest request) {
        UserResponseDto response = userWebMapper.toResponse(createUserUseCase.create(
                new CreateUserCommand(request.email(), request.password(), request.name(),
                        request.surname(), request.phoneNumber(), request.activated(),
                        request.isActive(), request.roles(), request.organizationId(),
                        request.workspaceId())));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.created(response));
    }

    @Operation(summary = "Update a user")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<UserResponseDto>> update(
            @PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
        UserResponseDto response = userWebMapper.toResponse(updateUserUseCase.update(id,
                new UpdateUserCommand(request.name(), request.surname(), request.phoneNumber(),
                        request.activated(), request.isActive(), request.roles(),
                        request.organizationId(), request.workspaceId())));
        return ResponseEntity.ok(ApiResponseWrapper.ok(response));
    }

    @Operation(summary = "Delete a user")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<Void>> delete(@PathVariable UUID id) {
        deleteUserUseCase.delete(id);
        return ResponseEntity.ok(ApiResponseWrapper.ok((Void) null, "User deleted"));
    }
}
