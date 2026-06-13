package com.gucardev.springreactboilerplate.infra.config.tenant;

import com.gucardev.springreactboilerplate.infra.config.security.jwt.UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Establishes the per-request {@link TenantContext} after authentication: the organization comes
 * from the {@link UserPrincipal}, the active workspace from the {@code X-Workspace-Id} header, and
 * super-admin status from the {@code ROLE_ADMIN} authority (which bypasses tenant isolation). Always
 * clears the context at the end of the request.
 */
@Slf4j
@Component
public class TenantContextFilter extends OncePerRequestFilter {

    public static final String WORKSPACE_HEADER = "X-Workspace-Id";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal) {
                boolean superAdmin = principal.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                // A workspace-pinned user (employee) is locked to their workspace; otherwise the
                // active workspace comes from the header (an org-level user choosing one).
                UUID workspaceId = principal.getWorkspaceId() != null
                        ? principal.getWorkspaceId()
                        : parseUuid(request.getHeader(WORKSPACE_HEADER));
                TenantContextHolder.set(new TenantContext(principal.getOrganizationId(), workspaceId, superAdmin));
            }
            filterChain.doFilter(request, response);
        } finally {
            TenantContextHolder.clear();
        }
    }

    private UUID parseUuid(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return UUID.fromString(value.trim());
        } catch (IllegalArgumentException e) {
            log.debug("Ignoring malformed {} header: {}", WORKSPACE_HEADER, value);
            return null;
        }
    }
}
