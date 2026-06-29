package com.gucardev.springreactboilerplate.features.core.featureflag.adapter.in.web;

import com.gucardev.springreactboilerplate.features.core.featureflag.adapter.in.web.dto.FeatureFlagDto;
import com.gucardev.springreactboilerplate.features.core.featureflag.domain.model.FeatureFlagState;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper from the {@link FeatureFlagState} domain read model to its web response DTO.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FeatureFlagWebMapper {

    FeatureFlagDto toResponse(FeatureFlagState state);

    List<FeatureFlagDto> toResponseList(List<FeatureFlagState> states);
}
