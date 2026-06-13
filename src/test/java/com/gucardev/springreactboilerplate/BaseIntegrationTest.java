package com.gucardev.springreactboilerplate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

/**
 * Single base for HTTP-level tests. Boots the full application context with a MOCK servlet
 * environment and drives the controllers through {@link MockMvc}, which installs the real Spring
 * Security filter chain (via {@code @AutoConfigureMockMvc}) yet runs each request on the test
 * thread — so Spring Security's {@code @WithMockUser}/{@code @WithUserDetails} annotations work.
 *
 * <p>Authenticate via {@code @WithMockUser}/{@code @WithUserDetails} instead of minting real JWTs.
 * This layer deliberately does <em>not</em> exercise the JWT authentication filter with a real
 * token — that end-to-end (real server + real bearer) path is intended for browser/e2e (Playwright)
 * tests. Each test runs in a transaction that rolls back, so writes don't leak between tests or
 * pollute the shared context.
 *
 * <p>The {@code postJson}/{@code getJson}/{@code putJson}/{@code deleteJson} helpers send/receive
 * JSON, assert the HTTP status and return the parsed response body.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(NoOpCacheConfig.class)
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

    protected static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    protected MockMvc mockMvc;

    protected JsonNode postJson(String uri, Object body, int expectedStatus) throws Exception {
        return exchange(post(uri).contentType(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(body)), expectedStatus);
    }

    protected JsonNode putJson(String uri, Object body, int expectedStatus) throws Exception {
        return exchange(put(uri).contentType(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(body)), expectedStatus);
    }

    protected JsonNode getJson(String uri, int expectedStatus) throws Exception {
        return exchange(get(uri), expectedStatus);
    }

    protected JsonNode deleteJson(String uri, int expectedStatus) throws Exception {
        return exchange(delete(uri), expectedStatus);
    }

    private JsonNode exchange(MockHttpServletRequestBuilder request, int expectedStatus) throws Exception {
        MvcResult result = mockMvc.perform(request)
                .andExpect(status().is(expectedStatus))
                .andReturn();
        String body = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        return body.isBlank() ? MAPPER.createObjectNode() : MAPPER.readTree(body);
    }
}
