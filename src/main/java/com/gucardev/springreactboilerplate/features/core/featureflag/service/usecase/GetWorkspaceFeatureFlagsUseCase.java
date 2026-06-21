package com.gucardev.springreactboilerplate.features.core.featureflag.service.usecase;

import com.gucardev.springreactboilerplate.features.core.featureflag.model.dto.FeatureFlagDto;
import com.gucardev.springreactboilerplate.features.core.featureflag.service.FeatureFlagService;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContextHolder;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetWorkspaceFeatureFlagsUseCase {

    private final FeatureFlagService featureFlagService;

    public List<FeatureFlagDto> execute() {
        return featureFlagService.list(TenantContextHolder.requireWorkspaceId());
    }
}
