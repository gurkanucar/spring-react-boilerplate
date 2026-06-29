package com.gucardev.springreactboilerplate.features.example.application.port.in;

import com.gucardev.springreactboilerplate.features.example.domain.model.Example;

/**
 * Input port: activate an example (a domain state transition).
 */
public interface ActivateExampleUseCase {

    Example execute(Long id);
}
