package com.gucardev.springreactboilerplate.domain.otp;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.gucardev.springreactboilerplate.BaseIntegrationTest;
import com.gucardev.springreactboilerplate.domain.otp.enums.OtpSendingChannel;
import com.gucardev.springreactboilerplate.domain.otp.enums.OtpType;
import com.gucardev.springreactboilerplate.domain.otp.model.request.SendOtpRequest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

/**
 * Verifies the resend cooldown that throttles OTP send abuse. Enables a positive cooldown (the
 * suite default is 0) and asserts a back-to-back send is rejected.
 */
@TestPropertySource(properties = "otp.resend-cooldown-seconds=120")
class OtpResendCooldownIT extends BaseIntegrationTest {

    @Test
    void secondSendWithinCooldown_isRejected() {
        SendOtpRequest send = SendOtpRequest.builder()
                .destination("+905559998877").type(OtpType.ACCOUNT_VERIFICATION)
                .sendingChannel(OtpSendingChannel.SMS).build();

        postJson("/otp/send", send, 200);

        JsonNode tooSoon = postJson("/otp/send", send, 429);
        assertThat(tooSoon.path("businessErrorCode").asText()).isEqualTo("OTP_RESEND_TOO_SOON");
    }
}
