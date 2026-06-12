package com.gucardev.springreactboilerplate.domain.example.usecase;

import com.gucardev.springreactboilerplate.domain.example.mapper.ExampleMapper;
import com.gucardev.springreactboilerplate.domain.example.model.dto.ExampleResponseDto;
import com.gucardev.springreactboilerplate.domain.example.model.request.CreateExampleRequest;
import com.gucardev.springreactboilerplate.domain.example.repository.ExampleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateExampleUseCase {

    private final ExampleRepository repository;
    private final ExampleMapper mapper;

    @Transactional
    public ExampleResponseDto execute(CreateExampleRequest request) {
        return mapper.toDto(repository.save(mapper.toEntity(request)));
    }
}
