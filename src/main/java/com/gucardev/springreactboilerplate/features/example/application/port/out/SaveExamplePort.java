package com.gucardev.springreactboilerplate.features.example.application.port.out;

import com.gucardev.springreactboilerplate.features.example.domain.model.Example;

/**
 * Output port: persist an example (insert or update) and return the stored state, including any
 * generated id and audit metadata.
 */
public interface SaveExamplePort {

    Example save(Example example);
}
