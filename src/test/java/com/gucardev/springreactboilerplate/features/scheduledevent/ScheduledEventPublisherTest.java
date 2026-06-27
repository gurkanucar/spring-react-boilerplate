package com.gucardev.springreactboilerplate.features.scheduledevent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.gucardev.springreactboilerplate.features.scheduledevent.model.message.ScheduledEventMessage;
import com.gucardev.springreactboilerplate.features.scheduledevent.publisher.ScheduledEventPublisher;
import com.gucardev.springreactboilerplate.infra.config.rabbitmq.RabbitMqConfig;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.core.MessagePostProcessor;

/**
 * Verifies the publisher sends to the delayed exchange/routing key and stamps the {@code x-delay}
 * header the plugin reads — the bit that makes the event "scheduled". No broker required.
 */
@ExtendWith(MockitoExtension.class)
class ScheduledEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ScheduledEventPublisher publisher;

    @Test
    void publish_sendsToDelayedExchange_withXDelayHeader() throws Exception {
        Instant now = Instant.now();
        ScheduledEventMessage message =
                new ScheduledEventMessage("id-1", "send-reminder", now, now.plusSeconds(10));

        publisher.publish(message, 10_000L);

        ArgumentCaptor<MessagePostProcessor> postProcessor =
                ArgumentCaptor.forClass(MessagePostProcessor.class);
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMqConfig.DELAYED_EXCHANGE),
                eq(RabbitMqConfig.SCHEDULED_EVENT_ROUTING_KEY),
                eq(message),
                postProcessor.capture());

        // Run the captured post-processor to confirm it sets the delay header on the outgoing message.
        Message amqpMessage = new Message(new byte[0], new MessageProperties());
        Message processed = postProcessor.getValue().postProcessMessage(amqpMessage);
        Object delayHeader = processed.getMessageProperties().getHeader(RabbitMqConfig.X_DELAY_HEADER);
        assertThat(delayHeader).isEqualTo(10_000L);
    }
}
