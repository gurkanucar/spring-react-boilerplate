package com.gucardev.springreactboilerplate.features.tenancy.organization.adapter.in.web;

import com.gucardev.springreactboilerplate.features.tenancy.organization.adapter.in.web.dto.CreateOrganizationRequest;
import com.gucardev.springreactboilerplate.features.tenancy.organization.adapter.in.web.dto.OrganizationFilterRequest;
import com.gucardev.springreactboilerplate.features.tenancy.organization.adapter.in.web.dto.OrganizationResponseDto;
import com.gucardev.springreactboilerplate.features.tenancy.organization.adapter.in.web.dto.UpdateOrganizationRequest;
import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.in.CreateOrganizationCommand;
import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.in.CreateOrganizationUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.in.DeleteOrganizationUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.in.GetOrganizationUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.in.OrganizationSearchQuery;
import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.in.SearchOrganizationsUseCase;
import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.in.UpdateOrganizationCommand;
import com.gucardev.springreactboilerplate.features.tenancy.organization.application.port.in.UpdateOrganizationUseCase;
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
 * Driving (web) adapter: global super-admin CRUD over organizations (tenants). Not tenant-scoped —
 * organizations are the tenant root, so only {@code ADMIN} manages them.
 *
 * <p>The controller only talks to input ports and maps between web DTOs and the domain model.
 */
@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
@Tag(name = "Organization", description = "Administer organizations (tenants).")
public class OrganizationController {

    private final CreateOrganizationUseCase createOrganizationUseCase;
    private final UpdateOrganizationUseCase updateOrganizationUseCase;
    private final DeleteOrganizationUseCase deleteOrganizationUseCase;
    private final GetOrganizationUseCase getOrganizationUseCase;
    private final SearchOrganizationsUseCase searchOrganizationsUseCase;
    private final OrganizationWebMapper organizationWebMapper;

    @Operation(summary = "List organizations (paged, sorted and filtered)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<List<OrganizationResponseDto>>> getAll(
            @Valid OrganizationFilterRequest filter) {
        OrganizationSearchQuery query = new OrganizationSearchQuery(
                filter.getName(), filter.getIsActive(), filter.getStartDate(), filter.getEndDate(),
                filter.toPageable());
        return ResponseEntity.ok(ApiResponseWrapper.ok(
                searchOrganizationsUseCase.search(query).map(organizationWebMapper::toResponse)));
    }

    @Operation(summary = "Get an organization by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<OrganizationResponseDto>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(
                organizationWebMapper.toResponse(getOrganizationUseCase.getById(id))));
    }

    @Operation(summary = "Create an organization")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<OrganizationResponseDto>> create(
            @Valid @RequestBody CreateOrganizationRequest request) {
        OrganizationResponseDto response = organizationWebMapper.toResponse(createOrganizationUseCase.create(
                new CreateOrganizationCommand(
                        request.name(),
                        request.slug(),
                        request.description(),
                        request.phoneNumber(),
                        request.address(),
                        request.isActive(),
                        request.logoId())));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.created(response));
    }

    @Operation(summary = "Update an organization")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<OrganizationResponseDto>> update(
            @PathVariable UUID id, @Valid @RequestBody UpdateOrganizationRequest request) {
        OrganizationResponseDto response = organizationWebMapper.toResponse(updateOrganizationUseCase.update(
                id,
                new UpdateOrganizationCommand(
                        request.name(),
                        request.slug(),
                        request.description(),
                        request.phoneNumber(),
                        request.address(),
                        request.isActive(),
                        request.logoId())));
        return ResponseEntity.ok(ApiResponseWrapper.ok(response));
    }

    @Operation(summary = "Delete an organization")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<Void>> delete(@PathVariable UUID id) {
        deleteOrganizationUseCase.delete(id);
        return ResponseEntity.ok(ApiResponseWrapper.ok((Void) null, "Organization deleted"));
    }
}
