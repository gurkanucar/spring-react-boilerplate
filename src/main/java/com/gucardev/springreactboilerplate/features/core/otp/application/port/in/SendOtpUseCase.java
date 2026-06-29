package com.gucardev.springreactboilerplate.features.core.otp.application.port.in;

import com.gucardev.springreactboilerplate.features.core.otp.domain.model.Otp;

/**
 * Input port: issue a fresh OTP for a {@code (destination, type)} pair and deliver it over the chosen
 * channel. Driving adapters depend on this interface, not on the implementing service. Returns the
 * persisted domain model; the code itself is never exposed to the client.
 */
public interface SendOtpUseCase {

    Otp send(SendOtpCommand command);
}
