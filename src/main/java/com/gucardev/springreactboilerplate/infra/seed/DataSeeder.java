package com.gucardev.springreactboilerplate.infra.seed;

import com.gucardev.springreactboilerplate.domain.user.entity.Role;
import com.gucardev.springreactboilerplate.domain.user.entity.User;
import com.gucardev.springreactboilerplate.domain.user.repository.RoleRepository;
import com.gucardev.springreactboilerplate.domain.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Ensures baseline roles exist on every startup and seeds a default admin so the API can be
 * exercised right away. Idempotent.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataSeeder {

    private static final String DEFAULT_ADMIN_EMAIL = "admin@mail.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "pass";

    static final String ROLE_USER = "USER";
    static final String ROLE_ADMIN = "ADMIN";

    private final PlatformTransactionManager transactionManager;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void seed() {
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        tx.executeWithoutResult(status -> {
            Map<String, Role> roles = seedRoles();
            seedDefaultAdmin(roles.get(ROLE_ADMIN));
        });
    }

    private Map<String, Role> seedRoles() {
        Map<String, Role> result = new LinkedHashMap<>();
        result.put(ROLE_USER, findOrCreateRole(ROLE_USER, "Regular User", "Standard end-user account"));
        result.put(ROLE_ADMIN, findOrCreateRole(ROLE_ADMIN, "Administrator", "Full system access"));
        return result;
    }

    private Role findOrCreateRole(String name, String displayName, String description) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role role = Role.builder()
                    .name(name)
                    .displayName(displayName)
                    .description(description)
                    .build();
            Role saved = roleRepository.save(role);
            log.info("Seeded role: {}", name);
            return saved;
        });
    }

    private void seedDefaultAdmin(Role adminRole) {
        if (userRepository.existsByEmail(DEFAULT_ADMIN_EMAIL)) {
            return;
        }
        User admin = User.builder()
                .email(DEFAULT_ADMIN_EMAIL)
                .password(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD))
                .name("Admin")
                .surname("Admin")
                .activated(true)
                .isActive(true)
                .roles(new HashSet<>(Set.of(adminRole)))
                .build();
        userRepository.save(admin);
        log.info("Seeded default admin: {}", DEFAULT_ADMIN_EMAIL);
    }
}
