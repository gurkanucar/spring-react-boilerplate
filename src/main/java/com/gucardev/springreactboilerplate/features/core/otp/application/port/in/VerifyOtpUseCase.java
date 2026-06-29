package com.gucardev.springreactboilerplate.features.core.otp.application.port.in;

/**
 * Input port: verify a submitted code against the active OTP for {@code (destination, type)}.
 */
public interface VerifyOtpUseCase {

    void verify(VerifyOtpCommand command);
}
