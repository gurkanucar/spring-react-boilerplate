package com.gucardev.springreactboilerplate.features.core.otp.service;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

/**
 * Generates a numeric OTP code of the requested length using {@link SecureRandom}.
 */
@Component
public class OtpCodeGenerator {

    private final SecureRandom random = new SecureRandom();

    public String generate(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
