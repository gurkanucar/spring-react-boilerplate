package com.gucardev.springreactboilerplate.domain.example.usecase;

import com.gucardev.springreactboilerplate.domain.example.entity.Example;
import com.gucardev.springreactboilerplate.domain.example.exception.ExampleExceptionType;
import com.gucardev.springreactboilerplate.domain.example.repository.ExampleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Shared lookup used by the read/update/delete use cases: returns the entity or throws a
 * domain {@code NOT_FOUND}. Keeps the "fetch or 404" logic in one place.
 */
@Service
@RequiredArgsConstructor
public class ExampleFinder {

    private final ExampleRepository repository;

    public Example findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ExampleExceptionType.NOT_FOUND.toException(id));
    }
}
