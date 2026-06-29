package com.gucardev.springreactboilerplate.features.core.otp.application.port.out;

import com.gucardev.springreactboilerplate.features.core.otp.domain.model.Otp;

/**
 * Output port: deliver an OTP to its destination over the channel recorded on the OTP. The application
 * core states the intent; the driven adapter owns the delivery mechanism (SMS/email/...).
 */
public interface SendOtpNotificationPort {

    void send(Otp otp);
}
