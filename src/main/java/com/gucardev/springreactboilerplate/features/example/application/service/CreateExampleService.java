package com.gucardev.springreactboilerplate.features.example.application.service;

import com.gucardev.springreactboilerplate.features.example.application.port.in.CreateExampleCommand;
import com.gucardev.springreactboilerplate.features.example.application.port.in.CreateExampleUseCase;
import com.gucardev.springreactboilerplate.features.example.application.port.out.SaveExamplePort;
import com.gucardev.springreactboilerplate.features.example.domain.model.Example;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateExampleService implements CreateExampleUseCase {

    private final SaveExamplePort saveExamplePort;

    @Override
    @Transactional
    public Example execute(CreateExampleCommand command) {
        return saveExamplePort.save(Example.builder()
                .name(command.name())
                .description(command.description())
                .active(command.active())
                .build());
    }
}
