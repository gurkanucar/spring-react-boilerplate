package com.gucardev.springreactboilerplate.features.example.application.port.in;

/**
 * Input port: delete an example by id.
 */
public interface DeleteExampleUseCase {

    void execute(Long id);
}
