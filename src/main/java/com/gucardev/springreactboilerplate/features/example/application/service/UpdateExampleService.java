package com.gucardev.springreactboilerplate.features.example.application.service;

import com.gucardev.springreactboilerplate.features.example.application.port.in.UpdateExampleCommand;
import com.gucardev.springreactboilerplate.features.example.application.port.in.UpdateExampleUseCase;
import com.gucardev.springreactboilerplate.features.example.application.port.out.SaveExamplePort;
import com.gucardev.springreactboilerplate.features.example.domain.model.Example;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateExampleService implements UpdateExampleUseCase {

    private final ExampleFinder finder;
    private final SaveExamplePort saveExamplePort;

    @Override
    @Transactional
    public Example execute(Long id, UpdateExampleCommand command) {
        Example example = finder.findById(id);
        // Partial update: null fields are left unchanged so updates don't wipe columns.
        if (command.name() != null) {
            example.setName(command.name());
        }
        if (command.description() != null) {
            example.setDescription(command.description());
        }
        if (command.active() != null) {
            example.setActive(command.active());
        }
        return saveExamplePort.save(example);
    }
}
