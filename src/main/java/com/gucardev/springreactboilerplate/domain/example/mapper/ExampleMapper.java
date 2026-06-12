package com.gucardev.springreactboilerplate.domain.example.mapper;

import com.gucardev.springreactboilerplate.domain.example.entity.Example;
import com.gucardev.springreactboilerplate.domain.example.model.dto.ExampleResponseDto;
import com.gucardev.springreactboilerplate.domain.example.model.request.CreateExampleRequest;
import com.gucardev.springreactboilerplate.domain.example.model.request.UpdateExampleRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper between {@link Example} and its DTOs. Unmapped target properties are
 * ignored (e.g. BaseDto's deletedAt/deletedBy, which have no entity counterpart), and null
 * request fields are skipped on update so partial updates don't wipe columns.
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ExampleMapper {

    ExampleResponseDto toDto(Example entity);

    Example toEntity(CreateExampleRequest request);

    void updateEntity(UpdateExampleRequest request, @MappingTarget Example entity);
}
