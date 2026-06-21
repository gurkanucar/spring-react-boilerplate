package com.gucardev.springreactboilerplate.features.example.service.usecase;

import com.gucardev.springreactboilerplate.features.example.entity.Example;
import com.gucardev.springreactboilerplate.features.example.mapper.ExampleMapper;
import com.gucardev.springreactboilerplate.features.example.model.dto.ExampleResponseDto;
import com.gucardev.springreactboilerplate.features.example.model.request.UpdateExampleRequest;
import com.gucardev.springreactboilerplate.features.example.repository.ExampleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateExampleUseCase {

    private final ExampleFinder finder;
    private final ExampleRepository repository;
    private final ExampleMapper exampleMapper;

    @Transactional
    public ExampleResponseDto execute(Long id, UpdateExampleRequest request) {
        Example example = finder.findById(id);
        exampleMapper.updateEntity(request, example);
        return exampleMapper.toDto(repository.save(example));
    }
}
