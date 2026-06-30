package com.gucardev.springreactboilerplate.features.core.otpv2redis.model.request;

import com.gucardev.springreactboilerplate.features.core.otp.enums.OtpSendingChannel;
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

/**
 * Request to verify a Redis-backed OTP. Unlike the DB variant, the Redis key is scoped by
 * {@code (type, channel, destination)}, so the channel must be supplied here too — it has to match
 * the channel the code was sent over.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to verify a Redis-backed OTP code.")
public class VerifyOtpRedisRequest {

    @NotBlank
    @Size(max = 255)
    @Schema(description = "Phone number or email — same value used when sending", requiredMode = Schema.RequiredMode.REQUIRED)
    private String destination;

    @NotNull
    @Schema(description = "OTP purpose — must match what was sent", example = "ACCOUNT_VERIFICATION", requiredMode = Schema.RequiredMode.REQUIRED)
    private OtpType type;

    @NotNull
    @Schema(description = "Delivery channel — must match what was sent", example = "SMS", requiredMode = Schema.RequiredMode.REQUIRED)
    private OtpSendingChannel sendingChannel;

    @NotBlank
    @Pattern(regexp = "^[0-9]{4,8}$", message = "OTP must be 4-8 digits")
    @Schema(description = "The OTP code the user entered", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String otp;
}
