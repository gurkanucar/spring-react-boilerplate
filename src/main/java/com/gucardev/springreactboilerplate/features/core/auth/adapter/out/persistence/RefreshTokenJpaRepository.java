package com.gucardev.springreactboilerplate.features.core.auth.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.shared.repository.BaseJpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data repository for {@link RefreshTokenJpaEntity}. An implementation detail of the
 * persistence adapter — the application core never sees it, only the refresh-token output ports.
 */
@Repository
public interface RefreshTokenJpaRepository extends BaseJpaRepository<RefreshTokenJpaEntity, Long> {

    /** Loads a token with its user and the user's roles, so a fresh access token can be minted. */
    @EntityGraph(attributePaths = {"user", "user.roles"})
    Optional<RefreshTokenJpaEntity> findByToken(String token);

    /** Purges revoked or expired tokens. Returns the number of rows removed. */
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshTokenJpaEntity rt WHERE rt.revoked = true OR rt.expiresAt < :now")
    int deleteRevokedOrExpired(@Param("now") LocalDateTime now);
}
