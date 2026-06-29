package com.gucardev.springreactboilerplate.features.core.user.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.shared.repository.BaseJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for {@link UserJpaEntity}. An implementation detail of the persistence
 * adapter — the application core never sees it, only the user output ports.
 */
@Repository
public interface UserJpaRepository extends BaseJpaRepository<UserJpaEntity, UUID> {

    /** Loads a user with roles eagerly so authorities can be built outside the persistence context. */
    @EntityGraph(attributePaths = "roles")
    Optional<UserJpaEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
