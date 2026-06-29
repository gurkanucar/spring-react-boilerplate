package com.gucardev.springreactboilerplate.features.news.application.port.out;

import com.gucardev.springreactboilerplate.features.news.domain.model.News;

/**
 * Output port: persist a news entry (insert or update) and return the stored state, including any
 * generated id and audit metadata.
 */
public interface SaveNewsPort {

    News save(News news);
}
