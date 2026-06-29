package com.gucardev.springreactboilerplate.features.example.adapter.in.web;

import com.gucardev.springreactboilerplate.features.example.adapter.in.web.dto.ExampleResponseDto;
import com.gucardev.springreactboilerplate.features.example.domain.model.Example;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper from the {@link Example} domain model to its web response DTO. Unmapped target
 * properties are ignored (e.g. BaseDto's deletedAt/deletedBy, which have no domain counterpart).
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExampleWebMapper {

    ExampleResponseDto toResponse(Example example);
}
