package com.gucardev.springreactboilerplate.features.example.repository;

import com.gucardev.springreactboilerplate.features.example.entity.Example;
import com.gucardev.springreactboilerplate.features.shared.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExampleRepository extends BaseJpaRepository<Example, Long> {
}
