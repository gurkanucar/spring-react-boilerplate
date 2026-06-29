package com.gucardev.springreactboilerplate.features.core.file.adapter.in.web;

import com.gucardev.springreactboilerplate.features.core.file.adapter.in.web.dto.FileResponse;
import com.gucardev.springreactboilerplate.features.core.file.adapter.in.web.dto.FileUrlResponse;
import com.gucardev.springreactboilerplate.features.core.file.adapter.in.web.dto.StorageBackendsResponse;
import com.gucardev.springreactboilerplate.features.core.file.domain.model.FileUrl;
import com.gucardev.springreactboilerplate.features.core.file.domain.model.StorageBackends;
import com.gucardev.springreactboilerplate.features.core.file.domain.model.StoredFile;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper from the file domain models to their web response DTOs. Unmapped target
 * properties are ignored (e.g. BaseDto's deletedAt/deletedBy, which have no domain counterpart).
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FileWebMapper {

    FileResponse toResponse(StoredFile file);

    FileUrlResponse toResponse(FileUrl url);

    StorageBackendsResponse toResponse(StorageBackends backends);
}
