package com.gucardev.springreactboilerplate.features.core.otp.adapter.out.code;

import com.gucardev.springreactboilerplate.features.core.otp.application.port.out.GenerateOtpCodePort;
import java.security.SecureRandom;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing {@link GenerateOtpCodePort}: generates a numeric OTP code of the requested
 * length using {@link SecureRandom}.
 */
@Component
public class SecureRandomOtpCodeAdapter implements GenerateOtpCodePort {

    private final SecureRandom random = new SecureRandom();

    @Override
    public String generate(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
