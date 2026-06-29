package com.gucardev.springreactboilerplate.features.news.application.port.in;

import com.gucardev.springreactboilerplate.features.news.domain.model.News;

/**
 * Input port: update an existing news entry within the active workspace.
 */
public interface UpdateNewsUseCase {

    News update(UpdateNewsCommand command);
}
