package com.gucardev.springreactboilerplate.features.tenancy.organization.adapter.in.web;

import com.gucardev.springreactboilerplate.features.tenancy.organization.adapter.in.web.dto.OrganizationResponseDto;
import com.gucardev.springreactboilerplate.features.tenancy.organization.domain.model.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper from the {@link Organization} domain model to its web response DTO. Unmapped target
 * properties are ignored (e.g. BaseDto's deletedAt/deletedBy, which have no domain counterpart).
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrganizationWebMapper {

    OrganizationResponseDto toResponse(Organization organization);
}
