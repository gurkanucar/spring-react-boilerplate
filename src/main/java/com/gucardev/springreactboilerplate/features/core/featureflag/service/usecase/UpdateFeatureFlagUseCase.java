package com.gucardev.springreactboilerplate.features.core.featureflag.service.usecase;

import com.gucardev.springreactboilerplate.features.core.featureflag.model.dto.FeatureFlagDto;
import com.gucardev.springreactboilerplate.features.core.featureflag.model.request.UpdateFeatureFlagRequest;
import com.gucardev.springreactboilerplate.features.core.featureflag.service.FeatureFlagService;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateFeatureFlagUseCase {

    private final FeatureFlagService featureFlagService;

    public FeatureFlagDto execute(String key, UpdateFeatureFlagRequest request) {
        return featureFlagService.set(TenantContextHolder.requireWorkspaceId(), key, request.enabled());
    }
}
