package com.gucardev.springreactboilerplate.features.core.otp.adapter.in.web;

import com.gucardev.springreactboilerplate.features.core.otp.adapter.in.web.dto.OtpResponseDto;
import com.gucardev.springreactboilerplate.features.core.otp.adapter.in.web.dto.SendOtpRequest;
import com.gucardev.springreactboilerplate.features.core.otp.adapter.in.web.dto.VerifyOtpRequest;
import com.gucardev.springreactboilerplate.features.core.otp.application.port.in.SendOtpCommand;
import com.gucardev.springreactboilerplate.features.core.otp.application.port.in.SendOtpUseCase;
import com.gucardev.springreactboilerplate.features.core.otp.application.port.in.VerifyOtpCommand;
import com.gucardev.springreactboilerplate.features.core.otp.application.port.in.VerifyOtpUseCase;
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
 *
 * <p>The controller only talks to input ports and maps between web DTOs and the domain model.
 */
@RestController
@RequestMapping("/otp")
@RequiredArgsConstructor
@Tag(name = "OTP", description = "Send and verify one-time passwords.")
public class OtpController {

    private final SendOtpUseCase sendOtpUseCase;
    private final VerifyOtpUseCase verifyOtpUseCase;
    private final OtpWebMapper otpWebMapper;

    @Operation(summary = "Send an OTP to a destination over the chosen channel")
    @PostMapping("/send")
    public ResponseEntity<ApiResponseWrapper<OtpResponseDto>> send(
            @Valid @RequestBody SendOtpRequest request) {
        OtpResponseDto response = otpWebMapper.toResponse(sendOtpUseCase.send(
                new SendOtpCommand(
                        request.getDestination(),
                        request.getType(),
                        request.getSendingChannel())));
        return ResponseEntity.ok(ApiResponseWrapper.ok(response));
    }

    @Operation(summary = "Verify a submitted OTP code")
    @PostMapping("/verify")
    public ResponseEntity<ApiResponseWrapper<Void>> verify(
            @Valid @RequestBody VerifyOtpRequest request) {
        verifyOtpUseCase.verify(new VerifyOtpCommand(
                request.getDestination(),
                request.getType(),
                request.getOtp()));
        return ResponseEntity.ok(ApiResponseWrapper.ok((Void) null, "OTP verified"));
    }
}
