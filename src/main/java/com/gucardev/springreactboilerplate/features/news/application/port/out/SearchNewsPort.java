package com.gucardev.springreactboilerplate.features.news.application.port.out;

import com.gucardev.springreactboilerplate.features.news.domain.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Output port: search news entries by criteria. Implemented by a driven persistence adapter that
 * translates the {@link NewsSearchCriteria} into a JPA {@code Specification}.
 */
public interface SearchNewsPort {

    Page<News> search(NewsSearchCriteria criteria, Pageable pageable);
}
