package com.gucardev.springreactboilerplate.features.news.application.port.in;

import java.util.UUID;

/**
 * Input port: delete a news entry within the active workspace.
 */
public interface DeleteNewsUseCase {

    void delete(UUID id);
}
