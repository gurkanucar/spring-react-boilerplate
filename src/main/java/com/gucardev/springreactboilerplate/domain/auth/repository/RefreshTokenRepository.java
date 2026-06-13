package com.gucardev.springreactboilerplate.domain.auth.repository;

import com.gucardev.springreactboilerplate.domain.auth.entity.RefreshToken;
import com.gucardev.springreactboilerplate.domain.shared.repository.BaseJpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RefreshTokenRepository extends BaseJpaRepository<RefreshToken, Long> {

    /** Loads a token with its user and the user's roles, so a fresh access token can be minted. */
    @EntityGraph(attributePaths = {"user", "user.roles"})
    Optional<RefreshToken> findByToken(String token);

    /** Purges revoked or expired tokens. Returns the number of rows removed. */
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken rt WHERE rt.revoked = true OR rt.expiresAt < :now")
    int deleteRevokedOrExpired(@Param("now") LocalDateTime now);
}
