package com.gucardev.springreactboilerplate.features.example.application.port.out;

import com.gucardev.springreactboilerplate.features.example.application.port.in.ExampleSearchCriteria;
import com.gucardev.springreactboilerplate.features.example.domain.model.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Output port: search examples by criteria. The driven persistence adapter translates the criteria
 * into a query specification and returns a page of domain models.
 */
public interface SearchExamplePort {

    Page<Example> search(ExampleSearchCriteria criteria, Pageable pageable);
}
