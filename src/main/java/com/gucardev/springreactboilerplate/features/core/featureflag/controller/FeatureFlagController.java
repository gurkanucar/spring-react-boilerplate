package com.gucardev.springreactboilerplate.features.core.featureflag.controller;

import com.gucardev.springreactboilerplate.features.core.featureflag.model.dto.FeatureFlagDto;
import com.gucardev.springreactboilerplate.features.core.featureflag.model.request.UpdateFeatureFlagRequest;
import com.gucardev.springreactboilerplate.features.core.featureflag.service.usecase.GetWorkspaceFeatureFlagsUseCase;
import com.gucardev.springreactboilerplate.features.core.featureflag.service.usecase.UpdateFeatureFlagUseCase;
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

@RestController
@RequestMapping("/api/v1/feature-flags")
@RequiredArgsConstructor
@Tag(name = "Feature Flags", description = "Per-workspace toggles for optional product features.")
public class FeatureFlagController {

    private final GetWorkspaceFeatureFlagsUseCase getWorkspaceFeatureFlagsUseCase;
    private final UpdateFeatureFlagUseCase updateFeatureFlagUseCase;

    @Operation(summary = "List feature flags (with effective values) for the current workspace")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER')")
    public ResponseEntity<ApiResponseWrapper<List<FeatureFlagDto>>> getFlags() {
        return ResponseEntity.ok(ApiResponseWrapper.ok(getWorkspaceFeatureFlagsUseCase.execute()));
    }

    @Operation(summary = "Enable or disable a feature flag for the current workspace")
    @PutMapping("/{key}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER')")
    public ResponseEntity<ApiResponseWrapper<FeatureFlagDto>> updateFlag(
            @PathVariable String key, @Valid @RequestBody UpdateFeatureFlagRequest request) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(updateFeatureFlagUseCase.execute(key, request)));
    }
}
