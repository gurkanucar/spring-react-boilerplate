package com.gucardev.springreactboilerplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.transaction.annotation.Transactional;

/**
 * Single base for HTTP-level integration tests. Boots the full application context in a MOCK
 * servlet environment and exposes a {@link RestTestClient} bound to it — so the real Spring Security
 * filter chain runs, yet requests execute on the test thread, which means {@code @WithMockUser}/
 * {@code @WithUserDetails} work and each test runs in a transaction that rolls back.
 *
 * <p>Use the {@code client} directly (status/jsonPath assertions, multipart, byte downloads), or the
 * {@code postJson}/{@code getJson}/{@code putJson}/{@code deleteJson} helpers, which send a request
 * <em>object</em> as JSON, assert the status and return the parsed response body.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureRestTestClient
@ActiveProfiles("test")
@Import(NoOpCacheConfig.class)
@Transactional
public abstract class BaseIntegrationTest {

    protected static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    protected RestTestClient client;

    protected JsonNode postJson(String uri, Object body, int expectedStatus) {
        return parse(client.post().uri(uri).contentType(MediaType.APPLICATION_JSON).body(body)
                .exchange().expectStatus().isEqualTo(expectedStatus)
                .expectBody().returnResult().getResponseBody());
    }

    protected JsonNode putJson(String uri, Object body, int expectedStatus) {
        return parse(client.put().uri(uri).contentType(MediaType.APPLICATION_JSON).body(body)
                .exchange().expectStatus().isEqualTo(expectedStatus)
                .expectBody().returnResult().getResponseBody());
    }

    protected JsonNode getJson(String uri, int expectedStatus) {
        return parse(client.get().uri(uri)
                .exchange().expectStatus().isEqualTo(expectedStatus)
                .expectBody().returnResult().getResponseBody());
    }

    protected JsonNode deleteJson(String uri, int expectedStatus) {
        return parse(client.delete().uri(uri)
                .exchange().expectStatus().isEqualTo(expectedStatus)
                .expectBody().returnResult().getResponseBody());
    }

    /** Multipart upload of a single {@code file} part plus optional string params; returns parsed body. */
    protected JsonNode uploadMultipart(String uri, byte[] fileBytes, String filename, MediaType fileType,
                                       Map<String, String> params, int expectedStatus) {
        return parse(multipart(uri, fileBytes, filename, fileType, params)
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody().returnResult().getResponseBody());
    }

    /**
     * Raw multipart exchange, for callers that need to assert headers/bytes themselves. The server
     * detects the real content type from the bytes (Tika), so the part is sent with the filename
     * only ({@code fileType} is accepted for readability but not transmitted per-part).
     */
    protected RestTestClient.ResponseSpec multipart(String uri, byte[] fileBytes, String filename,
                                                    MediaType fileType, Map<String, String> params) {
        ByteArrayResource fileResource = new ByteArrayResource(fileBytes) {
            @Override
            public String getFilename() {
                return filename;
            }
        };
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", fileResource);
        if (params != null) {
            params.forEach(parts::add);
        }
        return client.post().uri(uri)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(parts)
                .exchange();
    }

    private JsonNode parse(byte[] body) {
        try {
            return body == null || body.length == 0 ? MAPPER.createObjectNode() : MAPPER.readTree(body);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse JSON response body", e);
        }
    }
}
