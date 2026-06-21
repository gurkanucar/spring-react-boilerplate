package com.gucardev.springreactboilerplate.features.core.file.mapper;

import com.gucardev.springreactboilerplate.features.core.file.entity.StoredFile;
import com.gucardev.springreactboilerplate.features.core.file.model.dto.FileResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FileMapper {

    FileResponseDto toDto(StoredFile file);
}
