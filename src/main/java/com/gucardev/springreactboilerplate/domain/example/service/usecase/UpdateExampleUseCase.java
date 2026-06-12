package com.gucardev.springreactboilerplate.domain.example.service.usecase;

import com.gucardev.springreactboilerplate.domain.example.entity.Example;
import com.gucardev.springreactboilerplate.domain.example.mapper.ExampleMapper;
import com.gucardev.springreactboilerplate.domain.example.model.dto.ExampleResponseDto;
import com.gucardev.springreactboilerplate.domain.example.model.request.UpdateExampleRequest;
import com.gucardev.springreactboilerplate.domain.example.repository.ExampleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateExampleUseCase {

    private final ExampleFinder finder;
    private final ExampleRepository repository;
    private final ExampleMapper mapper;

    @Transactional
    public ExampleResponseDto execute(Long id, UpdateExampleRequest request) {
        Example example = finder.findById(id);
        mapper.updateEntity(request, example);
        return mapper.toDto(repository.save(example));
    }
}
