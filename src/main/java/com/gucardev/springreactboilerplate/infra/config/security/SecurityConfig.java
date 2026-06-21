package com.gucardev.springreactboilerplate.infra.config.security;

import com.gucardev.springreactboilerplate.infra.config.security.jwt.JwtAuthenticationFilter;
import com.gucardev.springreactboilerplate.infra.config.tenant.TenantContextFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Stateless REST security: JWT bearer authentication with no server-side session, returning the
 * standard {@link com.gucardev.springreactboilerplate.infra.exception.model.ApiError} envelope for
 * 401/403 instead of the default empty-body responses.
 *
 * <p>The chain runs {@link JwtAuthenticationFilter} (authenticates the bearer token and populates
 * the {@code SecurityContext}) followed by {@link TenantContextFilter} (derives the
 * organization/workspace scope from the authenticated principal). Browser login flows (HTTP basic,
 * form login, logout) are disabled; public paths are bound from {@code security.ignored-paths} and
 * method-level authorization is enabled via {@link EnableMethodSecurity}.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final TenantContextFilter tenantContextFilter;

    /** Public paths (Swagger, actuator, auth, public APIs, ws...) bound from application.yml. */
    @Value("${security.ignored-paths}")
    private String[] ignoredPaths;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ignoredPaths).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                // Stateless API: no browser login flows.
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        // After authentication so the principal is available to derive the tenant scope.
        http.addFilterAfter(tenantContextFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Exposes the {@link AuthenticationManager} (backed by the {@code CustomUserDetailsService} +
     * {@code BCryptPasswordEncoder} beans) so {@code LoginUseCase} can authenticate credentials.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * CORS for the frontend. Driven entirely by {@code app.cors.allowed-origins} (comma-separated)
     * from the active profile — dev sets {@code *} to allow any origin, prod lists explicit origins.
     * Values are treated as <em>origin patterns</em> ({@code setAllowedOriginPatterns}), so the
     * wildcard {@code *} is permitted even with {@code allowCredentials(true)} (a plain
     * {@code setAllowedOrigins("*")} is not).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins}") List<String> allowedOriginPatterns) {

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(allowedOriginPatterns);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
