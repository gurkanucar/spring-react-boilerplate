package com.gucardev.springreactboilerplate.features.core.featureflag.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.core.featureflag.application.port.out.DeleteFeatureFlagPort;
import com.gucardev.springreactboilerplate.features.core.featureflag.application.port.out.LoadFeatureFlagPort;
import com.gucardev.springreactboilerplate.features.core.featureflag.application.port.out.SaveFeatureFlagPort;
import com.gucardev.springreactboilerplate.features.core.featureflag.domain.model.FeatureFlag;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing the feature-flag load/save/delete output ports with Spring Data JPA. Maps
 * domain ⇄ entity at the boundary so the application core stays persistence-agnostic.
 */
@Component
@RequiredArgsConstructor
public class FeatureFlagPersistenceAdapter
        implements LoadFeatureFlagPort, SaveFeatureFlagPort, DeleteFeatureFlagPort {

    private final FeatureFlagJpaRepository repository;
    private final FeatureFlagPersistenceMapper mapper;

    @Override
    public List<FeatureFlag> findByWorkspaceId(UUID workspaceId) {
        return repository.findByWorkspaceId(workspaceId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<FeatureFlag> findByWorkspaceIdAndFlagKey(UUID workspaceId, String flagKey) {
        return repository.findByWorkspaceIdAndFlagKey(workspaceId, flagKey).map(mapper::toDomain);
    }

    @Override
    public FeatureFlag save(FeatureFlag featureFlag) {
        return mapper.toDomain(repository.save(mapper.toEntity(featureFlag)));
    }

    @Override
    public int deleteByWorkspaceId(UUID workspaceId) {
        return repository.deleteByWorkspaceId(workspaceId);
    }
}
