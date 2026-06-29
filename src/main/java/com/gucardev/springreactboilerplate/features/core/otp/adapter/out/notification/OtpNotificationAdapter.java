package com.gucardev.springreactboilerplate.features.core.otp.adapter.out.notification;

import com.gucardev.springreactboilerplate.features.core.otp.application.exception.OtpExceptionType;
import com.gucardev.springreactboilerplate.features.core.otp.application.port.out.SendOtpNotificationPort;
import com.gucardev.springreactboilerplate.features.core.otp.domain.model.Otp;
import com.gucardev.springreactboilerplate.features.core.otp.domain.model.OtpSendingChannel;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing {@link SendOtpNotificationPort}: routes an OTP to the {@link OtpSender}
 * registered for its channel. The map is built once from every {@code OtpSender} bean, so adding a
 * new channel implementation is enough.
 */
@Component
public class OtpNotificationAdapter implements SendOtpNotificationPort {

    private final Map<OtpSendingChannel, OtpSender> sendersByChannel;

    public OtpNotificationAdapter(List<OtpSender> senders) {
        this.sendersByChannel = new EnumMap<>(OtpSendingChannel.class);
        senders.forEach(sender -> sendersByChannel.put(sender.channel(), sender));
    }

    @Override
    public void send(Otp otp) {
        OtpSender sender = sendersByChannel.get(otp.getSendingChannel());
        if (sender == null) {
            throw OtpExceptionType.NO_SENDER.toException(otp.getSendingChannel());
        }
        sender.send(otp.getDestination(), otp.getCode(), otp.getType());
    }
}
