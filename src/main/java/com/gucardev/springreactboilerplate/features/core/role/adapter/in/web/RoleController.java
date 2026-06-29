package com.gucardev.springreactboilerplate.features.core.role.adapter.in.web;

import com.gucardev.springreactboilerplate.features.core.role.adapter.in.web.dto.CreateRoleRequest;
import com.gucardev.springreactboilerplate.features.core.role.adapter.in.web.dto.RoleFilterRequest;
import com.gucardev.springreactboilerplate.features.core.role.adapter.in.web.dto.RoleResponseDto;
import com.gucardev.springreactboilerplate.features.core.role.adapter.in.web.dto.UpdateRoleRequest;
import com.gucardev.springreactboilerplate.features.core.role.application.port.in.CreateRoleCommand;
import com.gucardev.springreactboilerplate.features.core.role.application.port.in.CreateRoleUseCase;
import com.gucardev.springreactboilerplate.features.core.role.application.port.in.DeleteRoleUseCase;
import com.gucardev.springreactboilerplate.features.core.role.application.port.in.GetAllRolesUseCase;
import com.gucardev.springreactboilerplate.features.core.role.application.port.in.GetRoleByIdUseCase;
import com.gucardev.springreactboilerplate.features.core.role.application.port.in.RoleSearchQuery;
import com.gucardev.springreactboilerplate.features.core.role.application.port.in.UpdateRoleCommand;
import com.gucardev.springreactboilerplate.features.core.role.application.port.in.UpdateRoleUseCase;
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
 * Driving (web) adapter for admin CRUD over roles. One injected input port per operation, responses
 * wrapped in the standard {@link ApiResponseWrapper}. The controller only talks to input ports and
 * maps between web DTOs and the domain model.
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
    private final RoleWebMapper roleWebMapper;

    @Operation(summary = "List roles (paged, sorted and filtered)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<List<RoleResponseDto>>> getAll(
            @Valid RoleFilterRequest filter) {
        RoleSearchQuery query = new RoleSearchQuery(
                filter.getName(), filter.getStartDate(), filter.getEndDate(), filter.toPageable());
        return ResponseEntity.ok(ApiResponseWrapper.ok(
                getAllRolesUseCase.getAll(query).map(roleWebMapper::toResponse)));
    }

    @Operation(summary = "Get a role by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<RoleResponseDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(
                roleWebMapper.toResponse(getRoleByIdUseCase.getById(id))));
    }

    @Operation(summary = "Create a role")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<RoleResponseDto>> create(
            @Valid @RequestBody CreateRoleRequest request) {
        RoleResponseDto response = roleWebMapper.toResponse(createRoleUseCase.create(
                new CreateRoleCommand(request.name(), request.displayName(), request.description())));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.created(response));
    }

    @Operation(summary = "Update a role")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<RoleResponseDto>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateRoleRequest request) {
        RoleResponseDto response = roleWebMapper.toResponse(updateRoleUseCase.update(
                id, new UpdateRoleCommand(request.displayName(), request.description())));
        return ResponseEntity.ok(ApiResponseWrapper.ok(response));
    }

    @Operation(summary = "Delete a role")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<Void>> delete(@PathVariable Long id) {
        deleteRoleUseCase.delete(id);
        return ResponseEntity.ok(ApiResponseWrapper.ok((Void) null, "Role deleted"));
    }
}
