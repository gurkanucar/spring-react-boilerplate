package com.gucardev.springreactboilerplate.features.core.featureflag.adapter.in.web;

import com.gucardev.springreactboilerplate.features.core.featureflag.adapter.in.web.dto.FeatureFlagDto;
import com.gucardev.springreactboilerplate.features.core.featureflag.adapter.in.web.dto.UpdateFeatureFlagRequest;
import com.gucardev.springreactboilerplate.features.core.featureflag.application.port.in.GetWorkspaceFeatureFlagsUseCase;
import com.gucardev.springreactboilerplate.features.core.featureflag.application.port.in.UpdateFeatureFlagCommand;
import com.gucardev.springreactboilerplate.features.core.featureflag.application.port.in.UpdateFeatureFlagUseCase;
import com.gucardev.springreactboilerplate.infra.config.response.ApiResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Driving (web) adapter for per-workspace feature flags. The controller only talks to input ports and
 * maps between web DTOs and the domain read model.
 */
@RestController
@RequestMapping("/api/v1/feature-flags")
@RequiredArgsConstructor
@Tag(name = "Feature Flags", description = "Per-workspace toggles for optional product features.")
public class FeatureFlagController {

    private final GetWorkspaceFeatureFlagsUseCase getWorkspaceFeatureFlagsUseCase;
    private final UpdateFeatureFlagUseCase updateFeatureFlagUseCase;
    private final FeatureFlagWebMapper featureFlagWebMapper;

    @Operation(summary = "List feature flags (with effective values) for the current workspace")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER')")
    public ResponseEntity<ApiResponseWrapper<List<FeatureFlagDto>>> getFlags() {
        return ResponseEntity.ok(ApiResponseWrapper.ok(
                featureFlagWebMapper.toResponseList(getWorkspaceFeatureFlagsUseCase.getForCurrentWorkspace())));
    }

    @Operation(summary = "Enable or disable a feature flag for the current workspace")
    @PutMapping("/{key}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER')")
    public ResponseEntity<ApiResponseWrapper<FeatureFlagDto>> updateFlag(
            @PathVariable String key, @Valid @RequestBody UpdateFeatureFlagRequest request) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(featureFlagWebMapper.toResponse(
                updateFeatureFlagUseCase.update(new UpdateFeatureFlagCommand(key, request.enabled())))));
    }
}
