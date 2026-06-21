package com.gucardev.springreactboilerplate.infra.config.mail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "app.mail.enabled=true",
        // The mocked JavaMailSender isn't a JavaMailSenderImpl, so the actuator mail health
        // contributor can't bind to it — disable it for this test.
        "management.health.mail.enabled=false"
})
class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @MockitoBean
    private JavaMailSender mailSender;

    @Test
    void rendersOtpTemplate_withCodeAndType() {
        String html = emailService.render("otp", Map.of("code", "987654", "type", "LOGIN_2FA"));
        assertThat(html).contains("987654").contains("LOGIN_2FA");
    }

    @Test
    void sendHtml_sendsMimeMessage_whenEnabled() {
        when(mailSender.createMimeMessage()).thenReturn(new JavaMailSenderImpl().createMimeMessage());

        emailService.sendHtml("user@example.com", "Your verification code", "otp",
                Map.of("code", "111111", "type", "ACCOUNT_VERIFICATION"));

        // sendHtml is @Async — allow the worker thread to deliver.
        verify(mailSender, timeout(3000)).send(any(MimeMessage.class));
    }
}
