package com.gucardev.springreactboilerplate.features.core.otp.application.port.out;

import com.gucardev.springreactboilerplate.features.core.otp.domain.model.Otp;
import com.gucardev.springreactboilerplate.features.core.otp.domain.model.OtpType;
import java.util.Optional;

/**
 * Output port: read OTPs from the store. Implemented by a driven persistence adapter.
 */
public interface LoadOtpPort {

    /** The most recent still-active OTP for a destination/type (there is at most one). */
    Optional<Otp> findActiveByDestinationAndType(String destination, OtpType type);

    /** The most recent OTP for a destination/type regardless of status (used for the resend cooldown). */
    Optional<Otp> findLatestByDestinationAndType(String destination, OtpType type);
}
