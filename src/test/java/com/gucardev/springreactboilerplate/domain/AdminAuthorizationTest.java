package com.gucardev.springreactboilerplate.domain;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gucardev.springreactboilerplate.BaseMockMvcTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * Authorization-rule tests for the admin User/Role endpoints, driven through MockMvc so
 * {@code @WithMockUser} applies. These verify the {@code @PreAuthorize("hasRole('ADMIN')")}
 * rules and the entry-point/deny behaviour — not JWT authentication itself (covered by the
 * real-token {@code AuthIT}/{@code UserRoleManagementIT}).
 */
class AdminAuthorizationTest extends BaseMockMvcTest {

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_canListUsers() throws Exception {
        mockMvc.perform(get("/api/v1/users")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_canListRoles() throws Exception {
        mockMvc.perform(get("/api/v1/roles")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_canCreateRole() throws Exception {
        mockMvc.perform(post("/api/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"TESTER\",\"displayName\":\"Tester\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "USER")
    void nonAdmin_isForbiddenFromUsers() throws Exception {
        mockMvc.perform(get("/api/v1/users")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void nonAdmin_isForbiddenFromCreatingRoles() throws Exception {
        mockMvc.perform(post("/api/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"X\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void anonymous_isUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/users")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/v1/roles")).andExpect(status().isUnauthorized());
    }
}
