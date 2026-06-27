package com.gucardev.springreactboilerplate.features.scheduledevent;

import static org.assertj.core.api.Assertions.assertThat;

import com.gucardev.springreactboilerplate.NoOpCacheConfig;
import com.gucardev.springreactboilerplate.features.scheduledevent.entity.ScheduledEvent;
import com.gucardev.springreactboilerplate.features.scheduledevent.entity.ScheduledEventStatus;
import com.gucardev.springreactboilerplate.features.scheduledevent.repository.ScheduledEventRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Confirms the {@code jsonb} payload round-trips through Hibernate's JSON mapping: a nested map is
 * persisted and read back equal after the persistence context is cleared (so it deserializes from
 * the DB, not the first-level cache).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@Import(NoOpCacheConfig.class)
@Transactional
class ScheduledEventJsonPersistenceIT {

    @Autowired
    private ScheduledEventRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void payload_roundTripsAsJson() {
        Instant now = Instant.now();
        ScheduledEvent saved = repository.saveAndFlush(ScheduledEvent.builder()
                .eventType("send-reminder")
                .payload(Map.of("userId", 42, "channel", "email", "meta", Map.of("retries", 3)))
                .status(ScheduledEventStatus.SCHEDULED)
                .delaySeconds(10L)
                .scheduledAt(now)
                .fireAt(now.plusSeconds(10))
                .attempts(0)
                .build());

        UUID id = saved.getId();
        entityManager.clear(); // drop the cached instance so the read comes from the DB

        ScheduledEvent reloaded = repository.findById(id).orElseThrow();
        assertThat(reloaded.getEventType()).isEqualTo("send-reminder");
        assertThat(reloaded.getPayload())
                .containsEntry("userId", 42)
                .containsEntry("channel", "email")
                .containsEntry("meta", Map.of("retries", 3));
        assertThat(reloaded.getStatus()).isEqualTo(ScheduledEventStatus.SCHEDULED);
    }

    @Test
    void typeOnlyEvent_persistsWithNullPayload() {
        Instant now = Instant.now();
        ScheduledEvent saved = repository.saveAndFlush(ScheduledEvent.builder()
                .eventType("daily-rollup")
                .payload(null) // type-only event — no payload
                .status(ScheduledEventStatus.SCHEDULED)
                .delaySeconds(60L)
                .scheduledAt(now)
                .fireAt(now.plusSeconds(60))
                .attempts(0)
                .build());

        UUID id = saved.getId();
        entityManager.clear();

        ScheduledEvent reloaded = repository.findById(id).orElseThrow();
        assertThat(reloaded.getEventType()).isEqualTo("daily-rollup");
        assertThat(reloaded.getPayload()).isNull();
    }
}
