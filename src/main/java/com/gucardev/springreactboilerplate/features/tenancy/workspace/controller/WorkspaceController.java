package com.gucardev.springreactboilerplate.features.tenancy.workspace.controller;

import com.gucardev.springreactboilerplate.features.tenancy.workspace.model.dto.WorkspaceResponseDto;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.model.request.CreateWorkspaceRequest;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.model.request.UpdateWorkspaceRequest;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.model.request.WorkspaceFilterRequest;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.service.usecase.CreateWorkspaceUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.service.usecase.DeleteWorkspaceUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.service.usecase.GetAllWorkspacesUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.service.usecase.GetWorkspaceByIdUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.service.usecase.UpdateWorkspaceUseCase;
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
 * Workspace CRUD, tenant-scoped to the caller's organization (a super-admin sees all). The active
 * organization is derived from the authenticated user; isolation is enforced in the use cases.
 */
@RestController
@RequestMapping("/api/v1/workspaces")
@RequiredArgsConstructor
@Tag(name = "Workspace", description = "Manage workspaces within an organization.")
public class WorkspaceController {

    private final CreateWorkspaceUseCase createWorkspaceUseCase;
    private final UpdateWorkspaceUseCase updateWorkspaceUseCase;
    private final DeleteWorkspaceUseCase deleteWorkspaceUseCase;
    private final GetWorkspaceByIdUseCase getWorkspaceByIdUseCase;
    private final GetAllWorkspacesUseCase getAllWorkspacesUseCase;

    @Operation(summary = "List workspaces in the caller's organization (paged, sorted and filtered)")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER')")
    public ResponseEntity<ApiResponseWrapper<List<WorkspaceResponseDto>>> getAll(
            @Valid WorkspaceFilterRequest filter) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(getAllWorkspacesUseCase.execute(filter)));
    }

    @Operation(summary = "Get a workspace by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER')")
    public ResponseEntity<ApiResponseWrapper<WorkspaceResponseDto>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(getWorkspaceByIdUseCase.execute(id)));
    }

    @Operation(summary = "Create a workspace")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER')")
    public ResponseEntity<ApiResponseWrapper<WorkspaceResponseDto>> create(
            @Valid @RequestBody CreateWorkspaceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.created(createWorkspaceUseCase.execute(request)));
    }

    @Operation(summary = "Update a workspace")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER')")
    public ResponseEntity<ApiResponseWrapper<WorkspaceResponseDto>> update(
            @PathVariable UUID id, @Valid @RequestBody UpdateWorkspaceRequest request) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(updateWorkspaceUseCase.execute(id, request)));
    }

    @Operation(summary = "Delete a workspace")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER')")
    public ResponseEntity<ApiResponseWrapper<Void>> delete(@PathVariable UUID id) {
        deleteWorkspaceUseCase.execute(id);
        return ResponseEntity.ok(ApiResponseWrapper.ok((Void) null, "Workspace deleted"));
    }
}
