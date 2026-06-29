package com.gucardev.springreactboilerplate.features.core.featureflag.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.shared.repository.BaseJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for {@link FeatureFlagJpaEntity}. An implementation detail of the persistence
 * adapter — the application core never sees it, only the feature-flag output ports.
 */
@Repository
public interface FeatureFlagJpaRepository extends BaseJpaRepository<FeatureFlagJpaEntity, UUID> {

    List<FeatureFlagJpaEntity> findByWorkspaceId(UUID workspaceId);

    Optional<FeatureFlagJpaEntity> findByWorkspaceIdAndFlagKey(UUID workspaceId, String flagKey);

    /** Remove every feature flag for a workspace — used when the workspace is deleted. */
    @Modifying
    @Query("delete from FeatureFlagJpaEntity f where f.workspaceId = :workspaceId")
    int deleteByWorkspaceId(@Param("workspaceId") UUID workspaceId);
}
