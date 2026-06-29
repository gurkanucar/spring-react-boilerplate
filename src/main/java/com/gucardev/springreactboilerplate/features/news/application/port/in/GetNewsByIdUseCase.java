package com.gucardev.springreactboilerplate.features.news.application.port.in;

import com.gucardev.springreactboilerplate.features.news.domain.model.News;
import java.util.UUID;

/**
 * Input port: read a single news entry by id within the active workspace.
 */
public interface GetNewsByIdUseCase {

    News getById(UUID id);
}
