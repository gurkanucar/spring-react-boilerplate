package com.gucardev.springreactboilerplate.infra.config.security;

import com.gucardev.springreactboilerplate.infra.config.security.jwt.UserPrincipal;
import com.gucardev.springreactboilerplate.infra.exception.CommonExceptionType;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/** Convenience accessors for the authenticated principal. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityUtils {

    /** The current user's id, or {@code null} when there is no authenticated {@link UserPrincipal}. */
    public static UUID currentUserIdOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            return principal.getId();
        }
        return null;
    }

    /** The current user's id; throws {@code FORBIDDEN} when unauthenticated. */
    public static UUID requireCurrentUserId() {
        UUID id = currentUserIdOrNull();
        if (id == null) {
            throw CommonExceptionType.FORBIDDEN.toException();
        }
        return id;
    }
}
