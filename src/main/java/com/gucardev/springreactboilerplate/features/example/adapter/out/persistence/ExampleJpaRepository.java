package com.gucardev.springreactboilerplate.features.example.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.shared.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for {@link ExampleJpaEntity}. An implementation detail of the persistence
 * adapter — the application core never sees it, only the load/save/delete/search output ports.
 */
@Repository
public interface ExampleJpaRepository extends BaseJpaRepository<ExampleJpaEntity, Long> {
}
