package com.gucardev.springreactboilerplate.features.example.application.service;

import com.gucardev.springreactboilerplate.features.example.application.port.in.ExampleSearchCriteria;
import com.gucardev.springreactboilerplate.features.example.application.port.in.GetAllExamplesUseCase;
import com.gucardev.springreactboilerplate.features.example.application.port.out.SearchExamplePort;
import com.gucardev.springreactboilerplate.features.example.domain.model.Example;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAllExamplesService implements GetAllExamplesUseCase {

    private final SearchExamplePort searchExamplePort;

    @Override
    @Transactional(readOnly = true)
    public Page<Example> execute(ExampleSearchCriteria criteria, Pageable pageable) {
        return searchExamplePort.search(criteria, pageable);
    }
}
