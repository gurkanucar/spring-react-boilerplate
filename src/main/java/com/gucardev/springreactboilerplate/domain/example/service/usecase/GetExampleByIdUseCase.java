package com.gucardev.springreactboilerplate.domain.example.service.usecase;

import com.gucardev.springreactboilerplate.domain.example.mapper.ExampleMapper;
import com.gucardev.springreactboilerplate.domain.example.model.dto.ExampleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetExampleByIdUseCase {

    private final ExampleFinder finder;
    private final ExampleMapper exampleMapper;

    @Transactional(readOnly = true)
    public ExampleResponseDto execute(Long id) {
        return exampleMapper.toDto(finder.findById(id));
    }
}
