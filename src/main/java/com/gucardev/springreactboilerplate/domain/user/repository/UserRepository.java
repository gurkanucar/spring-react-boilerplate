package com.gucardev.springreactboilerplate.domain.user.repository;

import com.gucardev.springreactboilerplate.domain.shared.repository.BaseJpaRepository;
import com.gucardev.springreactboilerplate.domain.user.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseJpaRepository<User, UUID> {

    /** Loads a user with roles eagerly so authorities can be built outside the persistence context. */
    @EntityGraph(attributePaths = "roles")
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
