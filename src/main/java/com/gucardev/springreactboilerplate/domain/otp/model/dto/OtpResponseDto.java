package com.gucardev.springreactboilerplate.domain.otp.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gucardev.springreactboilerplate.domain.otp.enums.OtpSendingChannel;
import com.gucardev.springreactboilerplate.domain.otp.enums.OtpType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Result of sending an OTP. The code itself is never returned in the response — only delivery metadata.")
public class OtpResponseDto {

    @Schema(description = "Destination it was sent to (echoed for confirmation)", example = "+905551234567")
    private String destination;

    @Schema(description = "Type / purpose", example = "ACCOUNT_VERIFICATION")
    private OtpType type;

    @Schema(description = "Channel used", example = "SMS")
    private OtpSendingChannel sendingChannel;

    @Schema(description = "When this OTP expires", example = "2026-05-26T15:05:00")
    private LocalDateTime expiryTime;
}
