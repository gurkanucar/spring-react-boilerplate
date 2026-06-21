package com.gucardev.springreactboilerplate.infra.config.security.jwt;

import com.gucardev.springreactboilerplate.features.core.user.entity.User;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Adapts a {@link User} to Spring Security's {@link UserDetails}. Role names are prefixed with
 * {@code ROLE_} so {@code hasRole(...)} checks match. {@code isEnabled()} reflects {@code isActive}.
 * Carries the user's {@code organizationId} so the tenant filter can scope the request.
 */
@Getter
@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {

    private final UUID id;
    private final UUID organizationId;
    private final UUID workspaceId;
    private final String email;
    private final String password;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;

    public static UserPrincipal from(User user) {
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .toList();
        return new UserPrincipal(
                user.getId(),
                user.getOrganizationId(),
                user.getWorkspaceId(),
                user.getEmail(),
                user.getPassword(),
                user.getIsActive(),
                authorities);
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
