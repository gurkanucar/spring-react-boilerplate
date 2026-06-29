package com.gucardev.springreactboilerplate.features.example.application.service;

import com.gucardev.springreactboilerplate.features.example.application.exception.ExampleExceptionType;
import com.gucardev.springreactboilerplate.features.example.application.port.out.LoadExamplePort;
import com.gucardev.springreactboilerplate.features.example.domain.model.Example;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Shared lookup used by the read/update/delete/activate use cases: returns the domain model or
 * throws a domain {@code NOT_FOUND}. Keeps the "fetch or 404" logic in one place.
 */
@Service
@RequiredArgsConstructor
public class ExampleFinder {

    private final LoadExamplePort loadExamplePort;

    public Example findById(Long id) {
        return loadExamplePort.findById(id)
                .orElseThrow(() -> ExampleExceptionType.NOT_FOUND.toException(id));
    }
}
