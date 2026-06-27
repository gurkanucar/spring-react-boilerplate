package com.gucardev.springreactboilerplate.infra.config.rabbitmq;

import java.util.Map;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wiring for the RabbitMQ scheduled/delayed-events example.
 *
 * <p>The exchange is an {@code x-delayed-message} exchange provided by the broker's
 * {@code rabbitmq_delayed_message_exchange} plugin (baked into the image — see
 * {@code docker/rabbitmq/Dockerfile}). A message published to it carries an {@code x-delay} header
 * (milliseconds); the broker parks the message and only routes it on to the bound queue once the
 * delay elapses. {@code x-delayed-type=direct} tells the exchange to route by routing key like a
 * normal direct exchange once the delay is up.
 *
 * <p>Declaring the {@link Queue}, {@link CustomExchange} and {@link Binding} as beans is enough:
 * Spring Boot's auto-configured {@code RabbitAdmin} declares them on the broker when a connection is
 * first established. The {@link MessageConverter} bean is auto-associated with both the
 * {@code RabbitTemplate} and the default listener container, so payloads travel as JSON.
 */
@Configuration
public class RabbitMqConfig {

    public static final String DELAYED_EXCHANGE = "scheduled.exchange";
    public static final String SCHEDULED_EVENTS_QUEUE = "scheduled.events.queue";
    public static final String SCHEDULED_EVENT_ROUTING_KEY = "scheduled.event";

    /** Header the delayed-message plugin reads to hold a message before routing it (milliseconds). */
    public static final String X_DELAY_HEADER = "x-delay";

    @Bean
    public Queue scheduledEventsQueue() {
        return new Queue(SCHEDULED_EVENTS_QUEUE, true);
    }

    @Bean
    public CustomExchange scheduledDelayedExchange() {
        // "x-delayed-message" is the plugin-provided exchange type; x-delayed-type picks the routing
        // semantics the exchange uses after the delay (direct = route by routing key).
        return new CustomExchange(DELAYED_EXCHANGE, "x-delayed-message", true, false,
                Map.of("x-delayed-type", "direct"));
    }

    @Bean
    public Binding scheduledEventBinding(Queue scheduledEventsQueue, CustomExchange scheduledDelayedExchange) {
        return BindingBuilder.bind(scheduledEventsQueue)
                .to(scheduledDelayedExchange)
                .with(SCHEDULED_EVENT_ROUTING_KEY)
                .noargs();
    }

    /** JSON (Jackson 3) on the wire instead of Java serialization. */
    @Bean
    public MessageConverter jacksonJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
