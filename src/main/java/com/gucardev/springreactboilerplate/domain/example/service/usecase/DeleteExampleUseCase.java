package com.gucardev.springreactboilerplate.domain.example.service.usecase;

import com.gucardev.springreactboilerplate.domain.example.repository.ExampleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteExampleUseCase {

    private final ExampleFinder finder;
    private final ExampleRepository repository;

    @Transactional
    public void execute(Long id) {
        repository.delete(finder.findById(id));
    }
}
