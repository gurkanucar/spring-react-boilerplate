package com.gucardev.springreactboilerplate.domain.example.repository;

import com.gucardev.springreactboilerplate.domain.example.entity.Example;
import com.gucardev.springreactboilerplate.domain.shared.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExampleRepository extends BaseJpaRepository<Example, Long> {
}
