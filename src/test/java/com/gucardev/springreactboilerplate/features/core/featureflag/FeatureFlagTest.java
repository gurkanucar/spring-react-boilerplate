package com.gucardev.springreactboilerplate.features.core.featureflag;

import static org.assertj.core.api.Assertions.assertThat;

import com.gucardev.springreactboilerplate.features.core.featureflag.model.dto.FeatureFlagDto;
import com.gucardev.springreactboilerplate.features.core.featureflag.service.FeatureFlagService;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.model.dto.WorkspaceResponseDto;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.model.request.CreateWorkspaceRequest;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.service.usecase.CreateWorkspaceUseCase;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContext;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContextHolder;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FeatureFlagTest {

    @Autowired
    private FeatureFlagService featureFlagService;
    @Autowired
    private CreateWorkspaceUseCase createWorkspace;

    @AfterEach
    void clearTenant() {
        TenantContextHolder.clear();
    }

    @Test
    void creatingWorkspace_seedsCatalogDefaultsEnabled() {
        TenantContextHolder.set(new TenantContext(UUID.randomUUID(), null, false));
        WorkspaceResponseDto ws = createWorkspace.execute(
                new CreateWorkspaceRequest("FF WS", "ff-ws", null, null, null, null, null, null, null));

        var flags = featureFlagService.list(ws.getId());
        assertThat(flags).extracting(FeatureFlagDto::key)
                .containsExactlyInAnyOrderElementsOf(FeatureFlags.KNOWN);
        assertThat(flags).allMatch(FeatureFlagDto::enabled);
        assertThat(flags).noneMatch(FeatureFlagDto::isDefault); // every flag now has a stored row
        assertThat(featureFlagService.isEnabled(ws.getId(), FeatureFlags.NEWS_MODULE)).isTrue();
    }

    @Test
    void unknownWorkspace_flagsDefaultToFalseAndMarkedDefault() {
        UUID workspace = UUID.randomUUID();
        assertThat(featureFlagService.isEnabled(workspace, FeatureFlags.NEWS_MODULE)).isFalse();
        assertThat(featureFlagService.list(workspace)).allMatch(FeatureFlagDto::isDefault);
    }

    @Test
    void set_overrides_andIsScopedPerWorkspace() {
        UUID workspaceA = UUID.randomUUID();
        UUID workspaceB = UUID.randomUUID();

        featureFlagService.set(workspaceA, FeatureFlags.NEWS_MODULE, true);

        assertThat(featureFlagService.isEnabled(workspaceA, FeatureFlags.NEWS_MODULE)).isTrue();
        assertThat(featureFlagService.isEnabled(workspaceB, FeatureFlags.NEWS_MODULE)).isFalse();

        // toggling back off is reflected immediately (cache evicted on write)
        featureFlagService.set(workspaceA, FeatureFlags.NEWS_MODULE, false);
        assertThat(featureFlagService.isEnabled(workspaceA, FeatureFlags.NEWS_MODULE)).isFalse();
    }

    @Test
    void customKey_isStoredAndListed() {
        UUID workspace = UUID.randomUUID();
        featureFlagService.set(workspace, "CUSTOM_FLAG", true);

        assertThat(featureFlagService.effectiveMap(workspace)).containsEntry("CUSTOM_FLAG", true);
        assertThat(featureFlagService.list(workspace)).anyMatch(f -> f.key().equals("CUSTOM_FLAG") && f.enabled());
    }
}
