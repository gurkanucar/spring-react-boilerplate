package com.gucardev.springreactboilerplate.features.example.application.service;

import com.gucardev.springreactboilerplate.features.example.application.port.in.DeleteExampleUseCase;
import com.gucardev.springreactboilerplate.features.example.application.port.out.DeleteExamplePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteExampleService implements DeleteExampleUseCase {

    private final ExampleFinder finder;
    private final DeleteExamplePort deleteExamplePort;

    @Override
    @Transactional
    public void execute(Long id) {
        deleteExamplePort.delete(finder.findById(id));
    }
}
