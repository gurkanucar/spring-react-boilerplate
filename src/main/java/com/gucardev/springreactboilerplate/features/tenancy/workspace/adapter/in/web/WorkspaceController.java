package com.gucardev.springreactboilerplate.features.tenancy.workspace.adapter.in.web;

import com.gucardev.springreactboilerplate.features.tenancy.workspace.adapter.in.web.dto.CreateWorkspaceRequest;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.adapter.in.web.dto.UpdateWorkspaceRequest;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.adapter.in.web.dto.WorkspaceFilterRequest;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.adapter.in.web.dto.WorkspaceResponseDto;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.in.CreateWorkspaceCommand;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.in.CreateWorkspaceUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.in.DeleteWorkspaceUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.in.GetWorkspaceUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.in.ListWorkspacesQuery;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.in.ListWorkspacesUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.in.UpdateWorkspaceCommand;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.application.port.in.UpdateWorkspaceUseCase;
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
 * Driving (web) adapter for workspaces, tenant-scoped to the caller's organization (a super-admin
 * sees all). The active organization is derived from the authenticated user; isolation is enforced in
 * the use cases.
 *
 * <p>The controller only talks to input ports and maps between web DTOs and the domain model.
 */
@RestController
@RequestMapping("/api/v1/workspaces")
@RequiredArgsConstructor
@Tag(name = "Workspace", description = "Manage workspaces within an organization.")
public class WorkspaceController {

    private final CreateWorkspaceUseCase createWorkspaceUseCase;
    private final UpdateWorkspaceUseCase updateWorkspaceUseCase;
    private final DeleteWorkspaceUseCase deleteWorkspaceUseCase;
    private final GetWorkspaceUseCase getWorkspaceUseCase;
    private final ListWorkspacesUseCase listWorkspacesUseCase;
    private final WorkspaceWebMapper workspaceWebMapper;

    @Operation(summary = "List workspaces in the caller's organization (paged, sorted and filtered)")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER')")
    public ResponseEntity<ApiResponseWrapper<List<WorkspaceResponseDto>>> getAll(
            @Valid WorkspaceFilterRequest filter) {
        ListWorkspacesQuery query = new ListWorkspacesQuery(
                filter.getName(),
                filter.getIsActive(),
                filter.getOrganizationId(),
                filter.getStartDate(),
                filter.getEndDate(),
                filter.toPageable());
        return ResponseEntity.ok(ApiResponseWrapper.ok(
                listWorkspacesUseCase.list(query).map(workspaceWebMapper::toResponse)));
    }

    @Operation(summary = "Get a workspace by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER')")
    public ResponseEntity<ApiResponseWrapper<WorkspaceResponseDto>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(
                workspaceWebMapper.toResponse(getWorkspaceUseCase.getById(id))));
    }

    @Operation(summary = "Create a workspace")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER')")
    public ResponseEntity<ApiResponseWrapper<WorkspaceResponseDto>> create(
            @Valid @RequestBody CreateWorkspaceRequest request) {
        CreateWorkspaceCommand command = new CreateWorkspaceCommand(
                request.name(),
                request.slug(),
                request.description(),
                request.phoneNumber(),
                request.address(),
                request.brandColor(),
                request.isActive(),
                request.logoId(),
                request.organizationId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.created(
                        workspaceWebMapper.toResponse(createWorkspaceUseCase.create(command))));
    }

    @Operation(summary = "Update a workspace")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER')")
    public ResponseEntity<ApiResponseWrapper<WorkspaceResponseDto>> update(
            @PathVariable UUID id, @Valid @RequestBody UpdateWorkspaceRequest request) {
        UpdateWorkspaceCommand command = new UpdateWorkspaceCommand(
                request.name(),
                request.slug(),
                request.description(),
                request.phoneNumber(),
                request.address(),
                request.brandColor(),
                request.isActive(),
                request.logoId());
        return ResponseEntity.ok(ApiResponseWrapper.ok(
                workspaceWebMapper.toResponse(updateWorkspaceUseCase.update(id, command))));
    }

    @Operation(summary = "Delete a workspace")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER')")
    public ResponseEntity<ApiResponseWrapper<Void>> delete(@PathVariable UUID id) {
        deleteWorkspaceUseCase.delete(id);
        return ResponseEntity.ok(ApiResponseWrapper.ok((Void) null, "Workspace deleted"));
    }
}
