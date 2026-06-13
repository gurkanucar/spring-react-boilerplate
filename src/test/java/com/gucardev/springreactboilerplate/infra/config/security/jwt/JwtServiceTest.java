package com.gucardev.springreactboilerplate.infra.config.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import com.gucardev.springreactboilerplate.domain.user.entity.Role;
import com.gucardev.springreactboilerplate.domain.user.entity.User;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties();
        props.setSecretKey("unit-test-secret-key-which-is-definitely-long-enough-0123456789");
        props.setTokenValidityInMinutes(60);
        props.setRefreshTokenValidityInMinutes(120);
        jwtService = new JwtService(props);
    }

    @Test
    void generatedToken_isValid_andExposesEmailSubject() {
        User user = sampleUser("jane@mail.com");

        String token = jwtService.generateAccessToken(user);

        assertThat(jwtService.isValid(token)).isTrue();
        assertThat(jwtService.extractEmail(token)).isEqualTo("jane@mail.com");
    }

    @Test
    void tamperedToken_isRejected() {
        String token = jwtService.generateAccessToken(sampleUser("jane@mail.com"));

        assertThat(jwtService.isValid(token + "tampered")).isFalse();
        assertThat(jwtService.isValid("garbage")).isFalse();
    }

    @Test
    void tokenSignedWithDifferentSecret_isRejected() {
        String token = jwtService.generateAccessToken(sampleUser("jane@mail.com"));

        JwtProperties otherProps = new JwtProperties();
        otherProps.setSecretKey("a-completely-different-secret-key-also-long-enough-9876543210");
        otherProps.setTokenValidityInMinutes(60);
        JwtService otherService = new JwtService(otherProps);

        assertThat(otherService.isValid(token)).isFalse();
    }

    private User sampleUser(String email) {
        Role role = Role.builder().name("USER").build();
        return User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .password("hashed")
                .roles(Set.of(role))
                .build();
    }
}
