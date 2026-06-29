package com.gucardev.springreactboilerplate.features.core.otp.application.port.out;

/**
 * Output port: produce a fresh OTP code. Wraps the random source so the application core does not
 * depend on a concrete RNG.
 */
public interface GenerateOtpCodePort {

    String generate(int length);
}
