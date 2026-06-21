package com.gucardev.springreactboilerplate.features.core.otp.model.request;

import com.gucardev.springreactboilerplate.features.core.otp.enums.OtpType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to verify an OTP code.")
public class VerifyOtpRequest {

    @NotBlank
    @Size(max = 255)
    @Schema(description = "Phone number or email — same value used when sending", requiredMode = Schema.RequiredMode.REQUIRED)
    private String destination;

    @NotNull
    @Schema(description = "OTP purpose — must match what was sent", example = "ACCOUNT_VERIFICATION", requiredMode = Schema.RequiredMode.REQUIRED)
    private OtpType type;

    @NotBlank
    @Pattern(regexp = "^[0-9]{4,8}$", message = "OTP must be 4-8 digits")
    @Schema(description = "The OTP code the user entered", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String otp;
}
