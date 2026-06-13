package com.gucardev.springreactboilerplate.domain.otp;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.gucardev.springreactboilerplate.BaseIntegrationTest;
import com.gucardev.springreactboilerplate.domain.otp.enums.OtpType;
import com.gucardev.springreactboilerplate.domain.otp.repository.OtpRepository;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Coverage of the public OTP send/verify flow. Since the code is never returned in a response, the
 * test reads it back from {@link OtpRepository} (stored as-is) to drive verification — the same
 * thing a real user does after receiving it out-of-band.
 */
class OtpIT extends BaseIntegrationTest {

    @Autowired
    private OtpRepository otpRepository;

    @Test
    void send_returnsMetadata_withoutTheCode() throws Exception {
        JsonNode data = postJson("/otp/send",
                Map.of("destination", "+905551234567", "type", "ACCOUNT_VERIFICATION", "sendingChannel", "SMS"),
                200).path("data");

        assertThat(data.path("destination").asText()).isEqualTo("+905551234567");
        assertThat(data.path("type").asText()).isEqualTo("ACCOUNT_VERIFICATION");
        assertThat(data.path("sendingChannel").asText()).isEqualTo("SMS");
        assertThat(data.path("expiryTime").asText()).isNotBlank();
        // The code must never be exposed.
        assertThat(data.has("code")).isFalse();
        assertThat(data.has("otp")).isFalse();
    }

    @Test
    void send_thenVerify_withCorrectCode_succeeds_andBurnsTheCode() throws Exception {
        postJson("/otp/send",
                Map.of("destination", "user@mail.com", "type", "ACCOUNT_VERIFICATION", "sendingChannel", "EMAIL"),
                200);
        String code = activeCode("user@mail.com", OtpType.ACCOUNT_VERIFICATION);

        postJson("/otp/verify",
                Map.of("destination", "user@mail.com", "type", "ACCOUNT_VERIFICATION", "otp", code), 200);

        // Single use: the same code no longer has an active OTP.
        JsonNode reused = postJson("/otp/verify",
                Map.of("destination", "user@mail.com", "type", "ACCOUNT_VERIFICATION", "otp", code), 404);
        assertThat(reused.path("businessErrorCode").asText()).isEqualTo("OTP_NO_ACTIVE");
    }

    @Test
    void verify_withWrongCode_returnsBadRequest() throws Exception {
        postJson("/otp/send",
                Map.of("destination", "+905550000000", "type", "LOGIN_2FA", "sendingChannel", "SMS"), 200);

        JsonNode body = postJson("/otp/verify",
                Map.of("destination", "+905550000000", "type", "LOGIN_2FA", "otp", "000000"), 400);
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("OTP_INVALID_CODE");
    }

    @Test
    void verify_withNoActiveOtp_returnsNotFound() throws Exception {
        JsonNode body = postJson("/otp/verify",
                Map.of("destination", "nobody@mail.com", "type", "PASSWORD_RESET", "otp", "123456"), 404);
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("OTP_NO_ACTIVE");
    }

    @Test
    void send_invalidatesPreviousActiveOtp() throws Exception {
        postJson("/otp/send",
                Map.of("destination", "dupe@mail.com", "type", "ACCOUNT_VERIFICATION", "sendingChannel", "EMAIL"), 200);
        postJson("/otp/send",
                Map.of("destination", "dupe@mail.com", "type", "ACCOUNT_VERIFICATION", "sendingChannel", "EMAIL"), 200);

        // Exactly one active OTP remains, and the latest code verifies.
        long active = otpRepository
                .findAll().stream()
                .filter(o -> o.getDestination().equals("dupe@mail.com") && !o.getUsed())
                .count();
        assertThat(active).isEqualTo(1);

        String latest = activeCode("dupe@mail.com", OtpType.ACCOUNT_VERIFICATION);
        postJson("/otp/verify",
                Map.of("destination", "dupe@mail.com", "type", "ACCOUNT_VERIFICATION", "otp", latest), 200);
    }

    @Test
    void verify_afterMaxWrongAttempts_locksOut() throws Exception {
        postJson("/otp/send",
                Map.of("destination", "lock@mail.com", "type", "PASSWORD_RESET", "sendingChannel", "EMAIL"), 200);

        // Default otp.max-attempts = 5 wrong guesses, all rejected as invalid...
        for (int i = 0; i < 5; i++) {
            postJson("/otp/verify",
                    Map.of("destination", "lock@mail.com", "type", "PASSWORD_RESET", "otp", "999999"), 400);
        }
        // ...then the OTP is locked even for the correct code.
        String correct = activeCode("lock@mail.com", OtpType.PASSWORD_RESET);
        JsonNode locked = postJson("/otp/verify",
                Map.of("destination", "lock@mail.com", "type", "PASSWORD_RESET", "otp", correct), 429);
        assertThat(locked.path("businessErrorCode").asText()).isEqualTo("OTP_MAX_ATTEMPTS");
    }

    private String activeCode(String destination, OtpType type) {
        return otpRepository
                .findFirstByDestinationAndTypeAndUsedFalseOrderByCreatedAtDesc(destination, type)
                .orElseThrow()
                .getCode();
    }
}
