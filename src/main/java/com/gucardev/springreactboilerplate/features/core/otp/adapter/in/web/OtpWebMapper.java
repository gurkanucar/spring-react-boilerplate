package com.gucardev.springreactboilerplate.features.core.otp.adapter.in.web;

import com.gucardev.springreactboilerplate.features.core.otp.adapter.in.web.dto.OtpResponseDto;
import com.gucardev.springreactboilerplate.features.core.otp.domain.model.Otp;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper from the {@link Otp} domain model to its web response DTO. Only delivery metadata
 * is surfaced — the code is never mapped onto the response.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OtpWebMapper {

    OtpResponseDto toResponse(Otp otp);
}
