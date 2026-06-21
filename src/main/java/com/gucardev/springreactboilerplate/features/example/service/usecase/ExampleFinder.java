package com.gucardev.springreactboilerplate.features.example.service.usecase;

import com.gucardev.springreactboilerplate.features.example.entity.Example;
import com.gucardev.springreactboilerplate.features.example.exception.ExampleExceptionType;
import com.gucardev.springreactboilerplate.features.example.repository.ExampleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Shared lookup used by the read/update/delete/activate use cases: returns the entity or
 * throws a domain {@code NOT_FOUND}. Keeps the "fetch or 404" logic in one place.
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
