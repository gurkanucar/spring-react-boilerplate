package com.gucardev.springreactboilerplate.features.outbox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gucardev.springreactboilerplate.features.outbox.config.OutboxProperties;
import com.gucardev.springreactboilerplate.features.outbox.entity.OutboxMessage;
import com.gucardev.springreactboilerplate.features.outbox.entity.OutboxStatus;
import com.gucardev.springreactboilerplate.features.outbox.model.message.OutboxEventEnvelope;
import com.gucardev.springreactboilerplate.features.outbox.relay.OutboxMessageSender;
import com.gucardev.springreactboilerplate.features.outbox.repository.OutboxMessageRepository;
import com.gucardev.springreactboilerplate.infra.config.rabbitmq.OutboxRabbitConfig;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * The relay's per-row publish step, broker-free: a successful publish marks the row PUBLISHED and
 * sends the right envelope; a broker failure is recorded (attempt + backoff) and left PENDING.
 */
@ExtendWith(MockitoExtension.class)
class OutboxMessageSenderTest {

    @Mock
    private OutboxMessageRepository repository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    private OutboxMessageSender sender;

    private OutboxMessage pendingMessage() {
        OutboxMessage m = OutboxMessage.builder()
                .id(UUID.randomUUID())
                .aggregateType("Order")
                .aggregateId("agg-1")
                .eventType("OrderCreated")
                .routingKey(OutboxRabbitConfig.ORDER_CREATED_ROUTING_KEY)
                .payload(Map.of("orderId", "agg-1"))
                .status(OutboxStatus.PENDING)
                .attempts(0)
                .build();
        return m;
    }

    private OutboxMessageSender senderWithDefaults() {
        return new OutboxMessageSender(repository, rabbitTemplate, new OutboxProperties());
    }

    @Test
    void send_publishesEnvelope_andMarksPublished() {
        OutboxMessage message = pendingMessage();
        when(repository.findById(message.getId())).thenReturn(Optional.of(message));
        sender = senderWithDefaults();

        sender.send(message.getId());

        ArgumentCaptor<OutboxEventEnvelope> envelope = ArgumentCaptor.forClass(OutboxEventEnvelope.class);
        verify(rabbitTemplate).convertAndSend(
                eq(OutboxRabbitConfig.OUTBOX_EXCHANGE),
                eq(OutboxRabbitConfig.ORDER_CREATED_ROUTING_KEY),
                envelope.capture(),
                any(MessagePostProcessor.class));
        assertThat(envelope.getValue().eventId()).isEqualTo(message.getId().toString());
        assertThat(envelope.getValue().eventType()).isEqualTo("OrderCreated");

        assertThat(message.getStatus()).isEqualTo(OutboxStatus.PUBLISHED);
        assertThat(message.getPublishedAt()).isNotNull();
        verify(repository).save(message);
    }

    @Test
    void send_onBrokerFailure_recordsAttempt_andKeepsPending() {
        OutboxMessage message = pendingMessage();
        when(repository.findById(message.getId())).thenReturn(Optional.of(message));
        doThrow(new AmqpException("broker down")).when(rabbitTemplate).convertAndSend(
                eq(OutboxRabbitConfig.OUTBOX_EXCHANGE), eq(OutboxRabbitConfig.ORDER_CREATED_ROUTING_KEY),
                any(OutboxEventEnvelope.class), any(MessagePostProcessor.class));
        sender = senderWithDefaults();

        sender.send(message.getId());

        assertThat(message.getStatus()).isEqualTo(OutboxStatus.PENDING);
        assertThat(message.getAttempts()).isEqualTo(1);
        assertThat(message.getLastError()).contains("broker down");
        assertThat(message.getNextAttemptAt()).isNotNull();
        verify(repository).save(message);
    }
}
