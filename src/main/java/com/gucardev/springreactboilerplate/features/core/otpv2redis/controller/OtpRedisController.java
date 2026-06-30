package com.gucardev.springreactboilerplate.features.core.otpv2redis.controller;

import com.gucardev.springreactboilerplate.features.core.otp.model.dto.OtpResponseDto;
import com.gucardev.springreactboilerplate.features.core.otp.model.request.SendOtpRequest;
import com.gucardev.springreactboilerplate.features.core.otpv2redis.model.request.VerifyOtpRedisRequest;
import com.gucardev.springreactboilerplate.features.core.otpv2redis.service.SendOtpRedisUseCase;
import com.gucardev.springreactboilerplate.features.core.otpv2redis.service.VerifyOtpRedisUseCase;
import com.gucardev.springreactboilerplate.infra.config.response.ApiResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Redis-backed OTP endpoints (send + verify). Functionally identical to {@code /otp} but the code
 * lives in Redis with a TTL instead of a DB row: it self-expires, needs no cleanup job, and works
 * across instances out of the box. The code is delivered out-of-band; responses carry metadata only.
 */
@RestController
@RequestMapping("/otp/v2")
@RequiredArgsConstructor
@Tag(name = "OTP v2 (Redis)", description = "Send and verify one-time passwords backed by Redis (TTL-based expiry).")
public class OtpRedisController {

    private final SendOtpRedisUseCase sendOtpRedisUseCase;
    private final VerifyOtpRedisUseCase verifyOtpRedisUseCase;

    @Operation(summary = "Send a Redis-backed OTP to a destination over the chosen channel")
    @PostMapping("/send")
    public ResponseEntity<ApiResponseWrapper<OtpResponseDto>> send(
            @Valid @RequestBody SendOtpRequest request) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(sendOtpRedisUseCase.execute(request)));
    }

    @Operation(summary = "Verify a submitted Redis-backed OTP code")
    @PostMapping("/verify")
    public ResponseEntity<ApiResponseWrapper<Void>> verify(
            @Valid @RequestBody VerifyOtpRedisRequest request) {
        verifyOtpRedisUseCase.execute(request);
        return ResponseEntity.ok(ApiResponseWrapper.ok((Void) null, "OTP verified"));
    }
}
