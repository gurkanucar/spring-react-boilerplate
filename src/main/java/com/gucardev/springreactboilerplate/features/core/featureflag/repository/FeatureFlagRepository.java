package com.gucardev.springreactboilerplate.features.core.featureflag.repository;

import com.gucardev.springreactboilerplate.features.core.featureflag.entity.FeatureFlag;
import com.gucardev.springreactboilerplate.features.shared.repository.BaseJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FeatureFlagRepository extends BaseJpaRepository<FeatureFlag, UUID> {

    List<FeatureFlag> findByWorkspaceId(UUID workspaceId);

    Optional<FeatureFlag> findByWorkspaceIdAndFlagKey(UUID workspaceId, String flagKey);

    /** Remove every feature flag for a workspace — used when the workspace is deleted. */
    @Modifying
    @Query("delete from FeatureFlag f where f.workspaceId = :workspaceId")
    int deleteByWorkspaceId(@Param("workspaceId") UUID workspaceId);
}
