package com.gucardev.springreactboilerplate.features.news.application.port.in;

import com.gucardev.springreactboilerplate.features.news.application.port.out.NewsSearchCriteria;
import com.gucardev.springreactboilerplate.features.news.domain.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Input port: list news in the active workspace, paged, sorted and filtered.
 */
public interface GetAllNewsUseCase {

    Page<News> getAll(NewsSearchCriteria criteria, Pageable pageable);
}
