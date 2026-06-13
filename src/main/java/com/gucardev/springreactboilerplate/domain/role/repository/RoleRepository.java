package com.gucardev.springreactboilerplate.domain.role.repository;

import com.gucardev.springreactboilerplate.domain.role.entity.Role;
import com.gucardev.springreactboilerplate.domain.shared.repository.BaseJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends BaseJpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    boolean existsByName(String name);
}
