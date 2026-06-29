package com.gucardev.springreactboilerplate.features.order.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.order.application.port.out.ProcessedEventPort;
import com.gucardev.springreactboilerplate.features.outbox.entity.ProcessedMessage;
import com.gucardev.springreactboilerplate.features.outbox.repository.ProcessedMessageRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing the {@link ProcessedEventPort} with the shared inbox table
 * ({@code processed_messages}). Keeps the order consumer decoupled from the outbox module's JPA types.
 */
@Component
@RequiredArgsConstructor
public class ProcessedEventAdapter implements ProcessedEventPort {

    private final ProcessedMessageRepository processedMessageRepository;

    @Override
    public boolean isProcessed(UUID eventId) {
        return processedMessageRepository.existsById(eventId);
    }

    @Override
    public void markProcessed(UUID eventId, String consumer) {
        processedMessageRepository.save(ProcessedMessage.builder()
                .id(eventId)
                .consumer(consumer)
                .processedAt(Instant.now())
                .build());
    }
}
