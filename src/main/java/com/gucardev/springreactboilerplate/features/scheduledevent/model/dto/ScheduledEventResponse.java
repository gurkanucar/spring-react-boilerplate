package com.gucardev.springreactboilerplate.features.scheduledevent.model.dto;

import com.gucardev.springreactboilerplate.features.scheduledevent.entity.ScheduledEventStatus;
import com.gucardev.springreactboilerplate.features.shared.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "A scheduled event with its lifecycle state and audit metadata.")
public class ScheduledEventResponse extends BaseDto {

    @Schema(description = "Identifier / correlation id carried in the message")
    private UUID id;

    @Schema(description = "Discriminator telling the consumer how to interpret the payload", example = "send-reminder")
    private String eventType;

    @Schema(description = "Structured JSON payload to act on when the event fires")
    private Map<String, Object> payload;

    @Schema(description = "Lifecycle status", example = "SCHEDULED")
    private ScheduledEventStatus status;

    @Schema(description = "Configured delay in seconds", example = "10")
    private Long delaySeconds;

    @Schema(description = "When the event was accepted/published")
    private Instant scheduledAt;

    @Schema(description = "When the broker is expected to deliver it")
    private Instant fireAt;

    @Schema(description = "When the listener handled it (null until delivered)")
    private Instant deliveredAt;

    @Schema(description = "When it was cancelled (null unless cancelled)")
    private Instant cancelledAt;

    @Schema(description = "Delivery attempts so far", example = "0")
    private Integer attempts;

    @Schema(description = "Last handler error, when status is FAILED")
    private String lastError;
}
