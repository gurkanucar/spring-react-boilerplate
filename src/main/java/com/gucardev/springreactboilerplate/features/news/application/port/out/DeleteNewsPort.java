package com.gucardev.springreactboilerplate.features.news.application.port.out;

import com.gucardev.springreactboilerplate.features.news.domain.model.News;

/**
 * Output port: delete a news entry from the store. Implemented by a driven persistence adapter.
 */
public interface DeleteNewsPort {

    void delete(News news);
}
