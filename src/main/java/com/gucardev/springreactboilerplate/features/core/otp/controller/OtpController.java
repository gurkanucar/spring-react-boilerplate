package com.gucardev.springreactboilerplate.features.core.otp.controller;

import com.gucardev.springreactboilerplate.features.core.otp.model.dto.OtpResponseDto;
import com.gucardev.springreactboilerplate.features.core.otp.model.request.SendOtpRequest;
import com.gucardev.springreactboilerplate.features.core.otp.model.request.VerifyOtpRequest;
import com.gucardev.springreactboilerplate.features.core.otp.service.usecase.SendOtpUseCase;
import com.gucardev.springreactboilerplate.features.core.otp.service.usecase.VerifyOtpUseCase;
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
 * Public OTP endpoints (send + verify), used by pre-auth flows such as account verification and
 * password reset. The code is delivered out-of-band; responses carry metadata only.
 */
@RestController
@RequestMapping("/otp")
@RequiredArgsConstructor
@Tag(name = "OTP", description = "Send and verify one-time passwords.")
public class OtpController {

    private final SendOtpUseCase sendOtpUseCase;
    private final VerifyOtpUseCase verifyOtpUseCase;

    @Operation(summary = "Send an OTP to a destination over the chosen channel")
    @PostMapping("/send")
    public ResponseEntity<ApiResponseWrapper<OtpResponseDto>> send(
            @Valid @RequestBody SendOtpRequest request) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(sendOtpUseCase.execute(request)));
    }

    @Operation(summary = "Verify a submitted OTP code")
    @PostMapping("/verify")
    public ResponseEntity<ApiResponseWrapper<Void>> verify(
            @Valid @RequestBody VerifyOtpRequest request) {
        verifyOtpUseCase.execute(request);
        return ResponseEntity.ok(ApiResponseWrapper.ok((Void) null, "OTP verified"));
    }
}
