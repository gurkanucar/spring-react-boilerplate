package com.gucardev.springreactboilerplate.domain.example.service.usecase;

import com.gucardev.springreactboilerplate.domain.example.mapper.ExampleMapper;
import com.gucardev.springreactboilerplate.domain.example.model.dto.ExampleResponseDto;
import com.gucardev.springreactboilerplate.domain.example.model.request.ExampleFilterRequest;
import com.gucardev.springreactboilerplate.domain.example.repository.ExampleRepository;
import com.gucardev.springreactboilerplate.domain.example.repository.specification.ExampleSpecification;
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
