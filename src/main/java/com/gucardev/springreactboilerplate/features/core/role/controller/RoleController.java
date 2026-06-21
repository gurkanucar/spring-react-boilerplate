package com.gucardev.springreactboilerplate.features.core.role.controller;

import com.gucardev.springreactboilerplate.features.core.role.model.dto.RoleResponseDto;
import com.gucardev.springreactboilerplate.features.core.role.model.request.CreateRoleRequest;
import com.gucardev.springreactboilerplate.features.core.role.model.request.RoleFilterRequest;
import com.gucardev.springreactboilerplate.features.core.role.model.request.UpdateRoleRequest;
import com.gucardev.springreactboilerplate.features.core.role.service.usecase.CreateRoleUseCase;
import com.gucardev.springreactboilerplate.features.core.role.service.usecase.DeleteRoleUseCase;
import com.gucardev.springreactboilerplate.features.core.role.service.usecase.GetAllRolesUseCase;
import com.gucardev.springreactboilerplate.features.core.role.service.usecase.GetRoleByIdUseCase;
import com.gucardev.springreactboilerplate.features.core.role.service.usecase.UpdateRoleUseCase;
import com.gucardev.springreactboilerplate.infra.config.response.ApiResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
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
 * Admin CRUD over roles. One injected use case per operation, responses wrapped in the standard
 * {@link ApiResponseWrapper}.
 */
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Role", description = "Administer grantable roles.")
public class RoleController {

    private final CreateRoleUseCase createRoleUseCase;
    private final UpdateRoleUseCase updateRoleUseCase;
    private final DeleteRoleUseCase deleteRoleUseCase;
    private final GetRoleByIdUseCase getRoleByIdUseCase;
    private final GetAllRolesUseCase getAllRolesUseCase;

    @Operation(summary = "List roles (paged, sorted and filtered)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<List<RoleResponseDto>>> getAll(
            @Valid RoleFilterRequest filter) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(getAllRolesUseCase.execute(filter)));
    }

    @Operation(summary = "Get a role by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<RoleResponseDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(getRoleByIdUseCase.execute(id)));
    }

    @Operation(summary = "Create a role")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<RoleResponseDto>> create(
            @Valid @RequestBody CreateRoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.created(createRoleUseCase.execute(request)));
    }

    @Operation(summary = "Update a role")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<RoleResponseDto>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateRoleRequest request) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(updateRoleUseCase.execute(id, request)));
    }

    @Operation(summary = "Delete a role")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<Void>> delete(@PathVariable Long id) {
        deleteRoleUseCase.execute(id);
        return ResponseEntity.ok(ApiResponseWrapper.ok((Void) null, "Role deleted"));
    }
}
