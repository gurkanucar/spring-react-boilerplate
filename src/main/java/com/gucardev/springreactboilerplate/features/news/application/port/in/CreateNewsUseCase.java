package com.gucardev.springreactboilerplate.features.news.application.port.in;

import com.gucardev.springreactboilerplate.features.news.domain.model.News;

/**
 * Input port: create a news entry in the active workspace. Driving adapters depend on this
 * interface, not on the implementing service.
 */
public interface CreateNewsUseCase {

    News create(CreateNewsCommand command);
}
