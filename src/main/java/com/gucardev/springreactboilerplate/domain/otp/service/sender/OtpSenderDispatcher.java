package com.gucardev.springreactboilerplate.domain.otp.service.sender;

import com.gucardev.springreactboilerplate.domain.otp.enums.OtpSendingChannel;
import com.gucardev.springreactboilerplate.domain.otp.enums.OtpType;
import com.gucardev.springreactboilerplate.domain.otp.exception.OtpExceptionType;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Routes an OTP to the {@link OtpSender} registered for the requested channel. The map is built
 * once from every {@code OtpSender} bean, so adding a new channel implementation is enough.
 */
@Component
public class OtpSenderDispatcher {

    private final Map<OtpSendingChannel, OtpSender> sendersByChannel;

    public OtpSenderDispatcher(List<OtpSender> senders) {
        this.sendersByChannel = new EnumMap<>(OtpSendingChannel.class);
        senders.forEach(sender -> sendersByChannel.put(sender.channel(), sender));
    }

    public void send(OtpSendingChannel channel, String destination, String code, OtpType type) {
        OtpSender sender = sendersByChannel.get(channel);
        if (sender == null) {
            throw OtpExceptionType.NO_SENDER.toException(channel);
        }
        sender.send(destination, code, type);
    }
}
