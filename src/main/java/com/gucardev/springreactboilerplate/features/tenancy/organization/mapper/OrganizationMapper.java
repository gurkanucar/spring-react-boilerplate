package com.gucardev.springreactboilerplate.features.tenancy.organization.mapper;

import com.gucardev.springreactboilerplate.features.tenancy.organization.entity.Organization;
import com.gucardev.springreactboilerplate.features.tenancy.organization.model.dto.OrganizationResponseDto;
import com.gucardev.springreactboilerplate.features.tenancy.organization.model.request.CreateOrganizationRequest;
import com.gucardev.springreactboilerplate.features.tenancy.organization.model.request.UpdateOrganizationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrganizationMapper {

    OrganizationResponseDto toDto(Organization organization);

    @Mapping(target = "id", ignore = true)
    Organization toEntity(CreateOrganizationRequest request);

    void updateEntity(UpdateOrganizationRequest request, @MappingTarget Organization organization);
}
