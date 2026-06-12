package com.gucardev.springreactboilerplate.infra.seed;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Ensures baseline roles exist on every startup and seeds a default admin in dev so
 * the API can be exercised right away. Idempotent.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataSeeder {

    private static final String DEFAULT_ADMIN_EMAIL = "admin@mail.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "pass";

    static final String ROLE_USER = "USER";
    static final String ROLE_ADMIN = "ADMIN";
    static final String ROLE_ORGANIZATION_MANAGER = "ORGANIZATION_MANAGER";
    static final String ROLE_MANAGER = "MANAGER";
    static final String ROLE_WAITER = "WAITER";
    static final String ROLE_CASHIER = "CASHIER";

    private final PlatformTransactionManager transactionManager;
//    private final RoleRepository roleRepository;
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void seed() {
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        tx.executeWithoutResult(status -> {
//            Map<String, Role> roles = seedRoles();
//            seedDefaultAdmin(roles.get(ROLE_ADMIN));
        });
    }
//
//    private Map<String, Role> seedRoles() {
//        Map<String, Role> result = new LinkedHashMap<>();
//        result.put(ROLE_USER, findOrCreateRole(ROLE_USER, "Regular User", "Standard end-user account"));
//        result.put(ROLE_ADMIN, findOrCreateRole(ROLE_ADMIN, "Administrator", "Full system access"));
//        result.put(ROLE_ORGANIZATION_MANAGER, findOrCreateRole(ROLE_ORGANIZATION_MANAGER, "Organization Manager", "Manages all workspaces of a single organization"));
//        result.put(ROLE_MANAGER, findOrCreateRole(ROLE_MANAGER, "Manager", "Workspace manager — manages areas, tables, menu, staff"));
//        result.put(ROLE_WAITER, findOrCreateRole(ROLE_WAITER, "Waiter", "Floor staff — assigned to one or more areas"));
//        result.put(ROLE_CASHIER, findOrCreateRole(ROLE_CASHIER, "Cashier", "Fast sell — rings up sales and views receipts"));
//        return result;
//    }
//
//    private Role findOrCreateRole(String name, String displayName, String description) {
//        return roleRepository.findByName(name).orElseGet(() -> {
//            Role r = new Role();
//            r.setName(name);
//            r.setDisplayName(displayName);
//            r.setDescription(description);
//            Role saved = roleRepository.save(r);
//            log.info("Seeded role: {}", name);
//            return saved;
//        });
//    }
//
//    private void seedDefaultAdmin(Role adminRole) {
//        if (userRepository.findByEmail(DEFAULT_ADMIN_EMAIL).isPresent()) {
//            return;
//        }
//        User u = new User();
//        u.setEmail(DEFAULT_ADMIN_EMAIL);
//        u.setPassword(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
//        u.setName("Admin");
//        u.setSurname("Admin");
//        u.setActivated(true);
//        u.setRoles(new HashSet<>(Set.of(adminRole)));
//        userRepository.save(u);
//        log.info("Seeded default admin: {}", DEFAULT_ADMIN_EMAIL);
//    }
}
