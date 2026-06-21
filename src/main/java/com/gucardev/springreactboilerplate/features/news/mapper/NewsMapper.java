package com.gucardev.springreactboilerplate.features.news.mapper;

import com.gucardev.springreactboilerplate.features.news.entity.News;
import com.gucardev.springreactboilerplate.features.news.model.dto.NewsResponseDto;
import com.gucardev.springreactboilerplate.features.news.model.request.CreateNewsRequest;
import com.gucardev.springreactboilerplate.features.news.model.request.UpdateNewsRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NewsMapper {

    NewsResponseDto toDto(News news);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "workspaceId", ignore = true)
    @Mapping(target = "slug", ignore = true)
    News toEntity(CreateNewsRequest request);

    // Collections and the featured image are applied explicitly in the use case so that a provided
    // value replaces (rather than merges into) the existing one; a null leaves it untouched.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "workspaceId", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "imageIds", ignore = true)
    @Mapping(target = "featuredImageId", ignore = true)
    @Mapping(target = "attachmentIds", ignore = true)
    @Mapping(target = "tags", ignore = true)
    void updateEntity(UpdateNewsRequest request, @MappingTarget News news);
}
