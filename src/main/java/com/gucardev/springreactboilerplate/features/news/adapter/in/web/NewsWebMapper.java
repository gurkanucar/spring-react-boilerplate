package com.gucardev.springreactboilerplate.features.news.adapter.in.web;

import com.gucardev.springreactboilerplate.features.news.adapter.in.web.dto.NewsResponseDto;
import com.gucardev.springreactboilerplate.features.news.domain.model.News;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper from the {@link News} domain model to its web response DTO. Unmapped target
 * properties are ignored (e.g. BaseDto's deletedAt/deletedBy, which have no domain counterpart).
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NewsWebMapper {

    NewsResponseDto toResponse(News news);
}
