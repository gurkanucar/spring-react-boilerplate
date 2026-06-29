package com.gucardev.springreactboilerplate.features.core.user.application.port.out;

/**
 * Output port: hash a plain-text password. Backed by a driven adapter that delegates to the
 * configured Spring Security {@code PasswordEncoder}, keeping the user core off that infrastructure.
 */
public interface PasswordEncoderPort {

    String encode(String rawPassword);
}
