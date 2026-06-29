package com.gucardev.springreactboilerplate.features.example.application.port.out;

import com.gucardev.springreactboilerplate.features.example.domain.model.Example;
import java.util.Optional;

/**
 * Output port: load an example from the store. Implemented by a driven persistence adapter.
 */
public interface LoadExamplePort {

    Optional<Example> findById(Long id);
}
