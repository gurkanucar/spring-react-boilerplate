package com.gucardev.springreactboilerplate.features.example.application.port.in;

import com.gucardev.springreactboilerplate.features.example.domain.model.Example;

/**
 * Input port: read a single example by id.
 */
public interface GetExampleByIdUseCase {

    Example execute(Long id);
}
