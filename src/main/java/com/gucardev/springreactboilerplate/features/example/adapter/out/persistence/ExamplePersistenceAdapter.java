package com.gucardev.springreactboilerplate.features.example.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.example.application.port.in.ExampleSearchCriteria;
import com.gucardev.springreactboilerplate.features.example.application.port.out.DeleteExamplePort;
import com.gucardev.springreactboilerplate.features.example.application.port.out.LoadExamplePort;
import com.gucardev.springreactboilerplate.features.example.application.port.out.SaveExamplePort;
import com.gucardev.springreactboilerplate.features.example.application.port.out.SearchExamplePort;
import com.gucardev.springreactboilerplate.features.example.domain.model.Example;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing the example load/save/delete/search output ports with Spring Data JPA.
 * Maps domain ⇄ entity at the boundary so the application core stays persistence-agnostic.
 */
@Component
@RequiredArgsConstructor
public class ExamplePersistenceAdapter
        implements LoadExamplePort, SaveExamplePort, DeleteExamplePort, SearchExamplePort {

    private final ExampleJpaRepository repository;
    private final ExamplePersistenceMapper mapper;

    @Override
    public Optional<Example> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Example save(Example example) {
        return mapper.toDomain(repository.save(mapper.toEntity(example)));
    }

    @Override
    public void delete(Example example) {
        repository.delete(mapper.toEntity(example));
    }

    @Override
    public Page<Example> search(ExampleSearchCriteria criteria, Pageable pageable) {
        return repository.findAll(ExampleSpecification.build(criteria), pageable)
                .map(mapper::toDomain);
    }
}
