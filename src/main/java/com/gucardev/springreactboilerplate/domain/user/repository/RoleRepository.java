package com.gucardev.springreactboilerplate.domain.user.repository;

import com.gucardev.springreactboilerplate.domain.shared.repository.BaseJpaRepository;
import com.gucardev.springreactboilerplate.domain.user.entity.Role;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends BaseJpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
}
