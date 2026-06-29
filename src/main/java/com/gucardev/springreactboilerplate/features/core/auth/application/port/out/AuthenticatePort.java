package com.gucardev.springreactboilerplate.features.core.auth.application.port.out;

/**
 * Output port: authenticate credentials. Backed by an adapter delegating to Spring Security's
 * {@code AuthenticationManager} (which raises {@code BadCredentialsException}/{@code DisabledException},
 * translated centrally), keeping the auth core off that infrastructure.
 */
public interface AuthenticatePort {

    void authenticate(String email, String password);
}
