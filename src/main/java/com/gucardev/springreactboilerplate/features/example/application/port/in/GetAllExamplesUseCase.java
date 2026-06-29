package com.gucardev.springreactboilerplate.features.example.application.port.in;

import com.gucardev.springreactboilerplate.features.example.domain.model.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Input port: list examples, paged/sorted and filtered by the given criteria.
 */
public interface GetAllExamplesUseCase {

    Page<Example> execute(ExampleSearchCriteria criteria, Pageable pageable);
}
