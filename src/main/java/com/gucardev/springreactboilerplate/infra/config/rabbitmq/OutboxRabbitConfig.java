package com.gucardev.springreactboilerplate.infra.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.boot.amqp.autoconfigure.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ wiring for the transactional-outbox example.
 *
 * <p>Topology: the relay publishes every outbox row to one durable {@code TopicExchange}
 * ({@link #OUTBOX_EXCHANGE}) under the row's routing key. The order consumer binds
 * {@link #ORDER_EVENTS_QUEUE} to {@code order.#}, so it receives {@code order.created} (and any future
 * {@code order.*}) events. The queue dead-letters to {@link #OUTBOX_DLX} → {@link #ORDER_EVENTS_DLQ}:
 * a message the consumer rejects (after its retries are exhausted) lands in the DLQ for inspection
 * rather than being dropped or requeued forever.
 *
 * <p>{@link #outboxRabbitListenerContainerFactory} is a dedicated listener factory for the outbox
 * consumer: it retries a handler a few times with backoff, then — because requeue-on-reject is off —
 * lets the broker dead-letter the message. It is isolated from the default factory so it can't change
 * the behaviour of the unrelated scheduled-events listener.
 */
@Configuration
public class OutboxRabbitConfig {

    public static final String OUTBOX_EXCHANGE = "outbox.exchange";

    /** Routing key the order producer's outbox rows carry; bound to the order consumer queue. */
    public static final String ORDER_CREATED_ROUTING_KEY = "order.created";

    public static final String ORDER_EVENTS_QUEUE = "order.events.queue";

    /** Dead-letter exchange + queue for order events the consumer ultimately rejects. */
    public static final String OUTBOX_DLX = "outbox.dlx";
    public static final String ORDER_EVENTS_DLQ = "order.events.dlq";

    public static final String LISTENER_FACTORY = "outboxRabbitListenerContainerFactory";

    @Bean
    public TopicExchange outboxExchange() {
        return new TopicExchange(OUTBOX_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange outboxDlx() {
        return new DirectExchange(OUTBOX_DLX, true, false);
    }

    @Bean
    public Queue orderEventsQueue() {
        // Reject (after retries) -> dead-letter to OUTBOX_DLX with the queue's own name as key.
        return QueueBuilder.durable(ORDER_EVENTS_QUEUE)
                .deadLetterExchange(OUTBOX_DLX)
                .deadLetterRoutingKey(ORDER_EVENTS_DLQ)
                .build();
    }

    @Bean
    public Queue orderEventsDlq() {
        return QueueBuilder.durable(ORDER_EVENTS_DLQ).build();
    }

    @Bean
    public Binding orderEventsBinding(Queue orderEventsQueue, TopicExchange outboxExchange) {
        // order.# catches order.created and any future order.* events on the same queue.
        return BindingBuilder.bind(orderEventsQueue).to(outboxExchange).with("order.#");
    }

    @Bean
    public Binding orderEventsDlqBinding(Queue orderEventsDlq, DirectExchange outboxDlx) {
        return BindingBuilder.bind(orderEventsDlq).to(outboxDlx).with(ORDER_EVENTS_DLQ);
    }

    /**
     * Retry policy for the outbox consumer: try the handler up to 3 times with exponential backoff,
     * then hand off to {@link RejectAndDontRequeueRecoverer}, which rejects without requeue so the
     * broker dead-letters it (requeue-on-reject is disabled on the factory below).
     */
    @Bean
    public MethodInterceptor outboxRetryInterceptor() {
        return RetryInterceptorBuilder.stateless()
                // up to 3 retries (after the first delivery) with exponential backoff: 1s, 2s, ... capped at 10s
                .maxRetries(3)
                .backOffOptions(1000, 2.0, 10000)
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build();
    }

    @Bean(LISTENER_FACTORY)
    public SimpleRabbitListenerContainerFactory outboxRabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            ConnectionFactory connectionFactory,
            MessageConverter jacksonJsonMessageConverter,
            MethodInterceptor outboxRetryInterceptor) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        // Apply Boot's spring.rabbitmq.listener.simple.* settings (e.g. auto-startup, concurrency) so
        // this factory behaves like the default one — including staying down in tests (no broker).
        configurer.configure(factory, connectionFactory);
        factory.setMessageConverter(jacksonJsonMessageConverter);
        // On reject, dead-letter instead of requeueing forever (which would spin a poison message).
        factory.setDefaultRequeueRejected(false);
        factory.setAdviceChain(outboxRetryInterceptor);
        return factory;
    }
}
