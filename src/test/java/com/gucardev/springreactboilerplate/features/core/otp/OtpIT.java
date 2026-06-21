package com.gucardev.springreactboilerplate.features.core.otp;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.gucardev.springreactboilerplate.BaseIntegrationTest;
import com.gucardev.springreactboilerplate.features.core.otp.enums.OtpSendingChannel;
import com.gucardev.springreactboilerplate.features.core.otp.enums.OtpType;
import com.gucardev.springreactboilerplate.features.core.otp.model.request.SendOtpRequest;
import com.gucardev.springreactboilerplate.features.core.otp.model.request.VerifyOtpRequest;
import com.gucardev.springreactboilerplate.features.core.otp.repository.OtpRepository;
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
    void send_returnsMetadata_withoutTheCode() {
        JsonNode data = postJson("/otp/send", SendOtpRequest.builder()
                .destination("+905551234567").type(OtpType.ACCOUNT_VERIFICATION)
                .sendingChannel(OtpSendingChannel.SMS).build(), 200).path("data");

        assertThat(data.path("destination").asText()).isEqualTo("+905551234567");
        assertThat(data.path("type").asText()).isEqualTo("ACCOUNT_VERIFICATION");
        assertThat(data.path("sendingChannel").asText()).isEqualTo("SMS");
        assertThat(data.path("expiryTime").asText()).isNotBlank();
        assertThat(data.has("code")).isFalse();
        assertThat(data.has("otp")).isFalse();
    }

    @Test
    void send_thenVerify_withCorrectCode_succeeds_andBurnsTheCode() {
        postJson("/otp/send", SendOtpRequest.builder()
                .destination("user@mail.com").type(OtpType.ACCOUNT_VERIFICATION)
                .sendingChannel(OtpSendingChannel.EMAIL).build(), 200);
        String code = activeCode("user@mail.com", OtpType.ACCOUNT_VERIFICATION);

        postJson("/otp/verify", VerifyOtpRequest.builder()
                .destination("user@mail.com").type(OtpType.ACCOUNT_VERIFICATION).otp(code).build(), 200);

        JsonNode reused = postJson("/otp/verify", VerifyOtpRequest.builder()
                .destination("user@mail.com").type(OtpType.ACCOUNT_VERIFICATION).otp(code).build(), 404);
        assertThat(reused.path("businessErrorCode").asText()).isEqualTo("OTP_NO_ACTIVE");
    }

    @Test
    void verify_withWrongCode_returnsBadRequest() {
        postJson("/otp/send", SendOtpRequest.builder()
                .destination("+905550000000").type(OtpType.LOGIN_2FA)
                .sendingChannel(OtpSendingChannel.SMS).build(), 200);

        JsonNode body = postJson("/otp/verify", VerifyOtpRequest.builder()
                .destination("+905550000000").type(OtpType.LOGIN_2FA).otp("000000").build(), 400);
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("OTP_INVALID_CODE");
    }

    @Test
    void verify_withNoActiveOtp_returnsNotFound() {
        JsonNode body = postJson("/otp/verify", VerifyOtpRequest.builder()
                .destination("nobody@mail.com").type(OtpType.PASSWORD_RESET).otp("123456").build(), 404);
        assertThat(body.path("businessErrorCode").asText()).isEqualTo("OTP_NO_ACTIVE");
    }

    @Test
    void send_invalidatesPreviousActiveOtp() {
        SendOtpRequest send = SendOtpRequest.builder()
                .destination("dupe@mail.com").type(OtpType.ACCOUNT_VERIFICATION)
                .sendingChannel(OtpSendingChannel.EMAIL).build();
        postJson("/otp/send", send, 200);
        postJson("/otp/send", send, 200);

        long active = otpRepository.findAll().stream()
                .filter(o -> o.getDestination().equals("dupe@mail.com") && !o.getUsed())
                .count();
        assertThat(active).isEqualTo(1);

        String latest = activeCode("dupe@mail.com", OtpType.ACCOUNT_VERIFICATION);
        postJson("/otp/verify", VerifyOtpRequest.builder()
                .destination("dupe@mail.com").type(OtpType.ACCOUNT_VERIFICATION).otp(latest).build(), 200);
    }

    @Test
    void verify_afterMaxWrongAttempts_locksOut() {
        postJson("/otp/send", SendOtpRequest.builder()
                .destination("lock@mail.com").type(OtpType.PASSWORD_RESET)
                .sendingChannel(OtpSendingChannel.EMAIL).build(), 200);

        for (int i = 0; i < 5; i++) {
            postJson("/otp/verify", VerifyOtpRequest.builder()
                    .destination("lock@mail.com").type(OtpType.PASSWORD_RESET).otp("999999").build(), 400);
        }
        String correct = activeCode("lock@mail.com", OtpType.PASSWORD_RESET);
        JsonNode locked = postJson("/otp/verify", VerifyOtpRequest.builder()
                .destination("lock@mail.com").type(OtpType.PASSWORD_RESET).otp(correct).build(), 429);
        assertThat(locked.path("businessErrorCode").asText()).isEqualTo("OTP_MAX_ATTEMPTS");
    }

    private String activeCode(String destination, OtpType type) {
        return otpRepository
                .findFirstByDestinationAndTypeAndUsedFalseOrderByCreatedAtDesc(destination, type)
                .orElseThrow()
                .getCode();
    }
}
