package com.gucardev.springreactboilerplate.features.tenancy.organization.controller;

import com.gucardev.springreactboilerplate.features.tenancy.organization.model.dto.OrganizationResponseDto;
import com.gucardev.springreactboilerplate.features.tenancy.organization.model.request.CreateOrganizationRequest;
import com.gucardev.springreactboilerplate.features.tenancy.organization.model.request.OrganizationFilterRequest;
import com.gucardev.springreactboilerplate.features.tenancy.organization.model.request.UpdateOrganizationRequest;
import com.gucardev.springreactboilerplate.features.tenancy.organization.service.usecase.CreateOrganizationUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.organization.service.usecase.DeleteOrganizationUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.organization.service.usecase.GetAllOrganizationsUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.organization.service.usecase.GetOrganizationByIdUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.organization.service.usecase.UpdateOrganizationUseCase;
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
 * Global super-admin CRUD over organizations (tenants). Not tenant-scoped — organizations are the
 * tenant root, so only {@code ADMIN} manages them.
 */
@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
@Tag(name = "Organization", description = "Administer organizations (tenants).")
public class OrganizationController {

    private final CreateOrganizationUseCase createOrganizationUseCase;
    private final UpdateOrganizationUseCase updateOrganizationUseCase;
    private final DeleteOrganizationUseCase deleteOrganizationUseCase;
    private final GetOrganizationByIdUseCase getOrganizationByIdUseCase;
    private final GetAllOrganizationsUseCase getAllOrganizationsUseCase;

    @Operation(summary = "List organizations (paged, sorted and filtered)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<List<OrganizationResponseDto>>> getAll(
            @Valid OrganizationFilterRequest filter) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(getAllOrganizationsUseCase.execute(filter)));
    }

    @Operation(summary = "Get an organization by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<OrganizationResponseDto>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(getOrganizationByIdUseCase.execute(id)));
    }

    @Operation(summary = "Create an organization")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<OrganizationResponseDto>> create(
            @Valid @RequestBody CreateOrganizationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.created(createOrganizationUseCase.execute(request)));
    }

    @Operation(summary = "Update an organization")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<OrganizationResponseDto>> update(
            @PathVariable UUID id, @Valid @RequestBody UpdateOrganizationRequest request) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(updateOrganizationUseCase.execute(id, request)));
    }

    @Operation(summary = "Delete an organization")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<Void>> delete(@PathVariable UUID id) {
        deleteOrganizationUseCase.execute(id);
        return ResponseEntity.ok(ApiResponseWrapper.ok((Void) null, "Organization deleted"));
    }
}
