package com.gucardev.springreactboilerplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * Base for HTTP-level integration tests. Boots the full context in a mock servlet
 * environment and exposes a {@link RestTestClient} bound to it. Runs under the {@code test}
 * profile (disposable in-memory H2, see {@code src/test/resources/application.properties}),
 * so no env vars or external services are required.
 *
 * <p>Extend this and inject endpoints via {@code @Import(MyController.class)} as needed.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Autowired
    protected RestTestClient client;
}
