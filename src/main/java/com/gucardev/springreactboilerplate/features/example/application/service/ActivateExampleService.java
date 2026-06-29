package com.gucardev.springreactboilerplate.features.example.application.service;

import com.gucardev.springreactboilerplate.features.example.application.port.in.ActivateExampleUseCase;
import com.gucardev.springreactboilerplate.features.example.application.port.out.SaveExamplePort;
import com.gucardev.springreactboilerplate.features.example.domain.model.Example;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Atomic operation: fetch via {@link ExampleFinder}, apply the domain activation rule, persist —
 * all in one transaction.
 */
@Service
@RequiredArgsConstructor
public class ActivateExampleService implements ActivateExampleUseCase {

    private final ExampleFinder finder;
    private final SaveExamplePort saveExamplePort;

    @Override
    @Transactional
    public Example execute(Long id) {
        Example example = finder.findById(id);
        example.activate();
        return saveExamplePort.save(example);
    }
}
