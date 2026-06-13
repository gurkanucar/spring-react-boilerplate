package com.gucardev.springreactboilerplate.infra.config.security.jwt;

import com.gucardev.springreactboilerplate.domain.user.repository.UserRepository;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheManagers;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheNames;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Loads a user (with roles, via the repository's entity graph) by email and adapts it to a
 * {@link UserPrincipal}. Backs both the login {@code AuthenticationManager} and the JWT filter —
 * which runs on every authenticated request, so the (immutable) principal is cached for a short TTL
 * to avoid a per-request DB hit. {@code UpdateUserUseCase}/{@code DeleteUserUseCase} evict it.
 *
 * <p>Trade-off: a role/enabled change propagates within the cache TTL ({@code CAFFEINE_1M}) on
 * instances that didn't process the change. Lower the TTL if you need faster propagation.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Cacheable(cacheNames = CacheNames.USERS, cacheManager = CacheManagers.CAFFEINE_1M, key = "#email")
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(UserPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("No user with email: " + email));
    }
}
