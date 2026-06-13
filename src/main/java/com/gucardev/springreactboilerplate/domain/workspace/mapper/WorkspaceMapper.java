package com.gucardev.springreactboilerplate.domain.workspace.mapper;

import com.gucardev.springreactboilerplate.domain.workspace.entity.Workspace;
import com.gucardev.springreactboilerplate.domain.workspace.model.dto.WorkspaceResponseDto;
import com.gucardev.springreactboilerplate.domain.workspace.model.request.CreateWorkspaceRequest;
import com.gucardev.springreactboilerplate.domain.workspace.model.request.UpdateWorkspaceRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WorkspaceMapper {

    WorkspaceResponseDto toDto(Workspace workspace);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organizationId", ignore = true)
    Workspace toEntity(CreateWorkspaceRequest request);

    @Mapping(target = "organizationId", ignore = true)
    void updateEntity(UpdateWorkspaceRequest request, @MappingTarget Workspace workspace);
}
