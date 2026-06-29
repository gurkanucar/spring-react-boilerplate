package com.gucardev.springreactboilerplate.features.core.otp.application.port.out;

import com.gucardev.springreactboilerplate.features.core.otp.domain.model.OtpType;

/**
 * Output port: mark every active OTP for a destination/type as used (called before issuing a new one).
 */
public interface InvalidateActiveOtpPort {

    int invalidateActive(String destination, OtpType type);
}
