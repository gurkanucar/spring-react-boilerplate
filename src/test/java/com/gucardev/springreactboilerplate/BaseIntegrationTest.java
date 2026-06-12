package com.gucardev.springreactboilerplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * Base for HTTP-level integration tests. Boots the full context on a real server (random
 * port) and exposes a {@link RestTestClient} bound to it. Runs under the {@code test}
 * profile (disposable in-memory H2, see {@code src/test/resources/application.properties}),
 * so no env vars or external services are required.
 *
 * <p>A real server is used (rather than a MOCK servlet environment) so the full stack runs —
 * most importantly the Spring Security filter chain, which a MOCK {@code RestTestClient} does
 * not install. Extend this and inject endpoints via {@code @Import(MyController.class)}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Autowired
    protected RestTestClient client;
}
