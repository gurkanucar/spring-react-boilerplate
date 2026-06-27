package com.gucardev.springreactboilerplate.features.scheduledevent.listener;

import com.gucardev.springreactboilerplate.features.scheduledevent.model.message.ScheduledEventMessage;
import com.gucardev.springreactboilerplate.features.scheduledevent.service.usecase.ProcessScheduledEventUseCase;
import com.gucardev.springreactboilerplate.infra.config.rabbitmq.RabbitMqConfig;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes scheduled events once the broker releases them (after their {@code x-delay} elapses) and
 * hands the id to {@link ProcessScheduledEventUseCase}, which loads the row and drives the state
 * machine (skip if cancelled/already delivered, else do the work). The JSON body is converted back
 * to {@link ScheduledEventMessage} by the {@code MessageConverter} bean in {@code RabbitMqConfig}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledEventListener {

    private final ProcessScheduledEventUseCase processScheduledEventUseCase;

    @RabbitListener(queues = RabbitMqConfig.SCHEDULED_EVENTS_QUEUE)
    public void onScheduledEvent(ScheduledEventMessage event) {
        log.info("[SCHEDULED-EVENT] received id={} type={} fireAt={}",
                event.id(), event.eventType(), event.fireAt());
        processScheduledEventUseCase.execute(UUID.fromString(event.id()));
    }
}
