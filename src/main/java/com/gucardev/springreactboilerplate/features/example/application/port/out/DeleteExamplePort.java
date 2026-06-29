package com.gucardev.springreactboilerplate.features.example.application.port.out;

import com.gucardev.springreactboilerplate.features.example.domain.model.Example;

/**
 * Output port: remove an example from the store.
 */
public interface DeleteExamplePort {

    void delete(Example example);
}
