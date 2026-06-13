package com.gucardev.springreactboilerplate.domain.otp;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.gucardev.springreactboilerplate.BaseMockMvcTest;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

/**
 * Verifies the resend cooldown that throttles OTP send abuse. Enables a positive cooldown (the
 * suite default is 0) and asserts a back-to-back send is rejected.
 */
@TestPropertySource(properties = "otp.resend-cooldown-seconds=120")
class OtpResendCooldownIT extends BaseMockMvcTest {

    @Test
    void secondSendWithinCooldown_isRejected() throws Exception {
        Map<String, String> body = Map.of(
                "destination", "+905559998877", "type", "ACCOUNT_VERIFICATION", "sendingChannel", "SMS");

        postJson("/otp/send", body, 200);

        JsonNode tooSoon = postJson("/otp/send", body, 429);
        assertThat(tooSoon.path("businessErrorCode").asText()).isEqualTo("OTP_RESEND_TOO_SOON");
    }
}
