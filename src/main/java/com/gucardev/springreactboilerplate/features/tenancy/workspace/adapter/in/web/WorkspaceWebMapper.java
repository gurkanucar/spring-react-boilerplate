package com.gucardev.springreactboilerplate.features.tenancy.workspace.adapter.in.web;

import com.gucardev.springreactboilerplate.features.tenancy.workspace.adapter.in.web.dto.WorkspaceResponseDto;
import com.gucardev.springreactboilerplate.features.tenancy.workspace.domain.model.Workspace;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper from the {@link Workspace} domain model to its web response DTO. Unmapped target
 * properties are ignored (e.g. BaseDto's deletedAt/deletedBy, which have no domain counterpart).
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WorkspaceWebMapper {

    WorkspaceResponseDto toResponse(Workspace workspace);
}
