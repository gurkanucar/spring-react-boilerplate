package com.gucardev.springreactboilerplate.infra.seed;

import com.gucardev.springreactboilerplate.features.core.role.adapter.out.persistence.RoleJpaEntity;
import com.gucardev.springreactboilerplate.features.core.role.adapter.out.persistence.RoleJpaRepository;
import com.gucardev.springreactboilerplate.features.core.user.application.port.in.CreateUserCommand;
import com.gucardev.springreactboilerplate.features.core.user.application.port.in.CreateUserUseCase;
import com.gucardev.springreactboilerplate.features.core.user.application.port.in.LoadUserByEmailUseCase;
import jakarta.annotation.PostConstruct;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Ensures baseline roles exist on every startup and seeds a default admin so the API can be
 * exercised right away. Idempotent. Roles are seeded directly via the role repository; the admin is
 * created through the user create use case (so it shares the same hashing/role-resolution logic).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataSeeder {

    private static final String DEFAULT_ADMIN_EMAIL = "admin@mail.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "pass";

    static final String ROLE_USER = "USER";
    static final String ROLE_ADMIN = "ADMIN";
    static final String ROLE_ORG_MANAGER = "ORG_MANAGER";
    static final String ROLE_WORKSPACE_USER = "WORKSPACE_USER";

    private final PlatformTransactionManager transactionManager;
    private final RoleJpaRepository roleRepository;
    private final CreateUserUseCase createUserUseCase;
    private final LoadUserByEmailUseCase loadUserByEmailUseCase;

    @PostConstruct
    public void seed() {
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        tx.executeWithoutResult(status -> {
            seedRoles();
            seedDefaultAdmin();
        });
    }

    private void seedRoles() {
        findOrCreateRole(ROLE_USER, "Regular User", "Standard end-user account");
        findOrCreateRole(ROLE_ADMIN, "Administrator", "Full system access");
        findOrCreateRole(ROLE_ORG_MANAGER, "Organization Manager", "Manages workspaces within an organization");
        findOrCreateRole(ROLE_WORKSPACE_USER, "Workspace User", "Workspace-level employee pinned to one workspace");
    }

    private void findOrCreateRole(String name, String displayName, String description) {
        roleRepository.findByName(name).orElseGet(() -> {
            RoleJpaEntity role = RoleJpaEntity.builder()
                    .name(name)
                    .displayName(displayName)
                    .description(description)
                    .build();
            RoleJpaEntity saved = roleRepository.save(role);
            log.info("Seeded role: {}", name);
            return saved;
        });
    }

    private void seedDefaultAdmin() {
        if (loadUserByEmailUseCase.loadByEmail(DEFAULT_ADMIN_EMAIL).isPresent()) {
            return;
        }
        createUserUseCase.create(new CreateUserCommand(
                DEFAULT_ADMIN_EMAIL,
                DEFAULT_ADMIN_PASSWORD,
                "Admin",
                "Admin",
                null,
                true,
                true,
                Set.of(ROLE_ADMIN),
                null,
                null));
        log.info("Seeded default admin: {}", DEFAULT_ADMIN_EMAIL);
    }
}
