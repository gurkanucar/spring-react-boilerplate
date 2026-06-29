package com.gucardev.springreactboilerplate.features.example.application.service;

import com.gucardev.springreactboilerplate.features.example.application.port.in.GetExampleByIdUseCase;
import com.gucardev.springreactboilerplate.features.example.domain.model.Example;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetExampleByIdService implements GetExampleByIdUseCase {

    private final ExampleFinder finder;

    @Override
    @Transactional(readOnly = true)
    public Example execute(Long id) {
        return finder.findById(id);
    }
}
