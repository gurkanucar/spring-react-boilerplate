package com.gucardev.springreactboilerplate.features.core.auth.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.core.auth.application.port.out.DeleteExpiredRefreshTokensPort;
import com.gucardev.springreactboilerplate.features.core.auth.application.port.out.LoadRefreshTokenPort;
import com.gucardev.springreactboilerplate.features.core.auth.application.port.out.SaveRefreshTokenPort;
import com.gucardev.springreactboilerplate.features.core.auth.domain.model.RefreshToken;
import com.gucardev.springreactboilerplate.features.core.user.adapter.out.persistence.UserJpaEntity;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing the refresh-token output ports with Spring Data JPA. Maps domain ⇄ entity
 * at the boundary and resolves the {@link UserJpaEntity} association from the domain model's
 * {@code userId} via a managed reference (no extra query), so the core stays persistence-agnostic.
 */
@Component
@RequiredArgsConstructor
public class RefreshTokenPersistenceAdapter
        implements LoadRefreshTokenPort, SaveRefreshTokenPort, DeleteExpiredRefreshTokensPort {

    private final RefreshTokenJpaRepository repository;
    private final RefreshTokenPersistenceMapper mapper;
    private final EntityManager entityManager;

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return repository.findByToken(token).map(mapper::toDomain);
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenJpaEntity entity = mapper.toEntity(refreshToken);
        entity.setUser(entityManager.getReference(UserJpaEntity.class, refreshToken.getUserId()));
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public int deleteRevokedOrExpired(LocalDateTime now) {
        return repository.deleteRevokedOrExpired(now);
    }
}
