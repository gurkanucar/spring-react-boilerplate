package com.gucardev.springreactboilerplate.features.example.application.port.in;

import com.gucardev.springreactboilerplate.features.example.domain.model.Example;

/**
 * Input port: create an example. Driving adapters depend on this interface, not on the
 * implementing service.
 */
public interface CreateExampleUseCase {

    Example execute(CreateExampleCommand command);
}
