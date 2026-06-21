package com.gucardev.springreactboilerplate.features.example.service.usecase;

import com.gucardev.springreactboilerplate.features.example.mapper.ExampleMapper;
import com.gucardev.springreactboilerplate.features.example.model.dto.ExampleResponseDto;
import com.gucardev.springreactboilerplate.features.example.model.request.CreateExampleRequest;
import com.gucardev.springreactboilerplate.features.example.repository.ExampleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateExampleUseCase {

    private final ExampleRepository repository;
    private final ExampleMapper exampleMapper;

    @Transactional
    public ExampleResponseDto execute(CreateExampleRequest request) {
        return exampleMapper.toDto(repository.save(exampleMapper.toEntity(request)));
    }
}
