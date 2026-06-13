package com.gucardev.springreactboilerplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base for fast, authorization-focused controller tests. Unlike {@link BaseIntegrationTest}
 * (real server + real JWT), this boots a MOCK servlet environment and drives the controllers
 * through {@link MockMvc}, so Spring Security's {@code @WithMockUser}/{@code @WithUserDetails}
 * test annotations work: the request is handled on the test thread, where the mocked
 * {@code SecurityContext} is visible.
 *
 * <p>Use this layer to assert role rules ({@code @PreAuthorize}) without minting real tokens. It
 * deliberately does <em>not</em> exercise the JWT authentication filter — for that, use a real
 * bearer token via {@link BaseIntegrationTest}. Each test runs in a transaction that rolls back,
 * so writes don't leak between tests or pollute the shared context.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseMockMvcTest {

    @Autowired
    protected MockMvc mockMvc;
}
