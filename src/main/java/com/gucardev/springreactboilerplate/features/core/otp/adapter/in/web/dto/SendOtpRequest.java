package com.gucardev.springreactboilerplate.features.core.otp.adapter.in.web.dto;

import com.gucardev.springreactboilerplate.features.core.otp.domain.model.OtpSendingChannel;
import com.gucardev.springreactboilerplate.features.core.otp.domain.model.OtpType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to send a new OTP. Invalidates any previously-active OTP for the same (destination, type) pair.")
public class SendOtpRequest {

    @NotBlank(message = "Destination is required")
    @Size(max = 255)
    @Schema(description = "Phone number for SMS, email address for EMAIL", example = "+905551234567", requiredMode = Schema.RequiredMode.REQUIRED)
    private String destination;

    @NotNull(message = "Type is required")
    @Schema(description = "OTP purpose", example = "ACCOUNT_VERIFICATION", requiredMode = Schema.RequiredMode.REQUIRED)
    private OtpType type;

    @NotNull(message = "Sending channel is required")
    @Schema(description = "Delivery channel", example = "SMS", requiredMode = Schema.RequiredMode.REQUIRED)
    private OtpSendingChannel sendingChannel;
}
