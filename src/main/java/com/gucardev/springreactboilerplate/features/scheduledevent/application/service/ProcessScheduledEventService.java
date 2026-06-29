package com.gucardev.springreactboilerplate.features.scheduledevent.application.service;

import com.gucardev.springreactboilerplate.features.scheduledevent.application.port.in.ProcessScheduledEventCommand;
import com.gucardev.springreactboilerplate.features.scheduledevent.application.port.in.ProcessScheduledEventUseCase;
import com.gucardev.springreactboilerplate.features.scheduledevent.application.port.out.LoadScheduledEventPort;
import com.gucardev.springreactboilerplate.features.scheduledevent.application.port.out.SaveScheduledEventPort;
import com.gucardev.springreactboilerplate.features.scheduledevent.domain.model.ScheduledEvent;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Drives a released event through its terminal state. Invoked by {@code ScheduledEventListener} once
 * the broker delivers the (delayed) message. The DB row is the source of truth, so:
 *
 * <ul>
 *   <li>missing row → message outlived its record (e.g. cancelled then purged) → drop it;</li>
 *   <li>{@code CANCELLED} → the user cancelled while it was in flight → skip (this is how the
 *       delayed-message plugin's "can't recall a message" limitation is worked around);</li>
 *   <li>{@code DELIVERED} → a redelivery of an already-handled message → skip (idempotency);</li>
 *   <li>otherwise → run the work, then mark {@code DELIVERED} or {@code FAILED}.</li>
 * </ul>
 *
 * <p>Failures are recorded and swallowed (the message is acked) rather than rethrown, so a poison
 * message can't loop forever in this demo. A real app would instead configure listener retry with
 * backoff and a dead-letter queue.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessScheduledEventService implements ProcessScheduledEventUseCase {

    private final LoadScheduledEventPort loadScheduledEventPort;
    private final SaveScheduledEventPort saveScheduledEventPort;

    @Override
    @Transactional
    public void process(ProcessScheduledEventCommand command) {
        UUID id = command.id();
        ScheduledEvent event = loadScheduledEventPort.findById(id).orElse(null);
        if (event == null) {
            log.warn("[SCHEDULED-EVENT] delivered id={} has no row (cancelled + purged?) — dropping", id);
            return;
        }
        if (event.isCancelled()) {
            log.info("[SCHEDULED-EVENT] id={} was cancelled — skipping", id);
            return;
        }
        if (event.isDelivered()) {
            log.info("[SCHEDULED-EVENT] id={} already delivered — skipping redelivery", id);
            return;
        }

        event.incrementAttempts();
        try {
            handle(event);
            event.markDelivered();
            log.info("[SCHEDULED-EVENT] delivered id={} attempts={}", id, event.getAttempts());
        } catch (Exception e) {
            event.markFailed(e.getMessage());
            log.error("[SCHEDULED-EVENT] failed id={} attempts={}", id, event.getAttempts(), e);
        }
        saveScheduledEventPort.save(event);
    }

    /**
     * The actual work to do when the event fires. {@code eventType} is the discriminator: branch on
     * it to know how to read the {@code jsonb} payload (which keys to expect, what to cast to). Swap
     * the cases for your real side effects.
     */
    private void handle(ScheduledEvent event) {
        // Payload is optional (type-only events carry none) — fall back to an empty map so lookups
        // are null-safe.
        Map<String, Object> payload = event.getPayload() == null ? Map.of() : event.getPayload();
        log.info("[SCHEDULED-EVENT] handling id={} type={} payload={} (fireAt={})",
                event.getId(), event.getEventType(), payload, event.getFireAt());

        switch (event.getEventType()) {
            case "send-reminder" -> {
                String channel = asString(payload.get("channel"));
                Long userId = asLong(payload.get("userId"));
                log.info("[SCHEDULED-EVENT] -> send reminder to userId={} via {}", userId, channel);
                // reminderService.send(userId, channel, ...)
            }
            case "expire-reservation" -> {
                String reservationId = asString(payload.get("reservationId"));
                log.info("[SCHEDULED-EVENT] -> expire reservation {}", reservationId);
                // reservationService.expire(reservationId)
            }
            default -> log.warn("[SCHEDULED-EVENT] no handler for type={} — payload kept as-is",
                    event.getEventType());
        }
    }

    private static String asString(Object value) {
        return value == null ? null : value.toString();
    }

    /** JSON numbers deserialize to Integer/Long/Double; normalize to Long for id-like fields. */
    private static Long asLong(Object value) {
        return value instanceof Number number ? number.longValue() : null;
    }
}
