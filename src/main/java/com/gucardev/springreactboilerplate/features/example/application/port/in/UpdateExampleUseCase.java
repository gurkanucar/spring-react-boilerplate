package com.gucardev.springreactboilerplate.features.example.application.port.in;

import com.gucardev.springreactboilerplate.features.example.domain.model.Example;

/**
 * Input port: update an example. Null command fields are left unchanged so partial updates don't
 * wipe columns.
 */
public interface UpdateExampleUseCase {

    Example execute(Long id, UpdateExampleCommand command);
}
