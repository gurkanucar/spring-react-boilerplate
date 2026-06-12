package com.gucardev.springreactboilerplate.domain.example.service.usecase;

import com.gucardev.springreactboilerplate.domain.example.entity.Example;
import com.gucardev.springreactboilerplate.domain.example.exception.ExampleExceptionType;
import com.gucardev.springreactboilerplate.domain.example.mapper.ExampleMapper;
import com.gucardev.springreactboilerplate.domain.example.model.dto.ExampleResponseDto;
import com.gucardev.springreactboilerplate.domain.example.repository.ExampleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Atomic operation: fetch via {@link ExampleFinder}, apply the activation rule, persist —
 * all in one transaction.
 */
@Service
@RequiredArgsConstructor
public class ActivateExampleUseCase {

    private final ExampleFinder finder;
    private final ExampleRepository repository;
    private final ExampleMapper mapper;

    @Transactional
    public ExampleResponseDto execute(Long id) {
        Example example = finder.findById(id);
        if (Boolean.TRUE.equals(example.getActive())) {
            throw ExampleExceptionType.ALREADY_ACTIVE.toException();
        }
        example.setActive(true);
        return mapper.toDto(repository.save(example));
    }
}
