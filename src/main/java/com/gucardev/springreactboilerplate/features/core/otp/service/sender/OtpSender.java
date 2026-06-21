package com.gucardev.springreactboilerplate.features.core.otp.service.sender;

import com.gucardev.springreactboilerplate.features.core.otp.enums.OtpSendingChannel;
import com.gucardev.springreactboilerplate.features.core.otp.enums.OtpType;

/**
 * Strategy for delivering an OTP over a specific channel. Add a real provider (Twilio, SES, SMTP,
 * ...) by implementing this interface and declaring it as a Spring bean — {@link OtpSenderDispatcher}
 * wires it in automatically, keyed by {@link #channel()}.
 */
public interface OtpSender {

    OtpSendingChannel channel();

    void send(String destination, String code, OtpType type);
}
