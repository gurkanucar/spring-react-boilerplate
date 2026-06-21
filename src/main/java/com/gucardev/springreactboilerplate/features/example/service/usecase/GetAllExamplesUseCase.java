package com.gucardev.springreactboilerplate.features.example.service.usecase;

import com.gucardev.springreactboilerplate.features.example.mapper.ExampleMapper;
import com.gucardev.springreactboilerplate.features.example.model.dto.ExampleResponseDto;
import com.gucardev.springreactboilerplate.features.example.model.request.ExampleFilterRequest;
import com.gucardev.springreactboilerplate.features.example.repository.ExampleRepository;
import com.gucardev.springreactboilerplate.features.example.repository.specification.ExampleSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAllExamplesUseCase {

    private final ExampleRepository repository;
    private final ExampleMapper exampleMapper;

    @Transactional(readOnly = true)
    public Page<ExampleResponseDto> execute(ExampleFilterRequest filter) {
        return repository.findAll(ExampleSpecification.build(filter), filter.toPageable())
                .map(exampleMapper::toDto);
    }
}
