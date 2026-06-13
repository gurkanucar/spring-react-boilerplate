package com.gucardev.springreactboilerplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
 *
 * <p>The {@code postJson}/{@code getJson} helpers send/receive JSON, assert the HTTP status and
 * return the parsed response body, so tests can focus on assertions rather than plumbing.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    protected static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    protected RestTestClient client;

    /** POSTs {@code payload} as JSON, asserts the status and returns the parsed response body. */
    protected JsonNode postJson(String uri, Object payload, int expectedStatus) {
        byte[] body = client.post().uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody().returnResult().getResponseBody();
        return readTree(body);
    }

    /** GETs {@code uri}, asserts the status and returns the parsed response body. */
    protected JsonNode getJson(String uri, int expectedStatus) {
        return getJson(uri, null, expectedStatus);
    }

    /** GETs {@code uri} with an optional bearer token, asserts the status and returns the body. */
    protected JsonNode getJson(String uri, String bearerToken, int expectedStatus) {
        RestTestClient.RequestHeadersSpec<?> request = client.get().uri(uri);
        if (bearerToken != null) {
            request = request.header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
        }
        byte[] body = request
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody().returnResult().getResponseBody();
        return readTree(body);
    }

    private JsonNode readTree(byte[] body) {
        try {
            return MAPPER.readTree(body);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse JSON response body", e);
        }
    }
}
