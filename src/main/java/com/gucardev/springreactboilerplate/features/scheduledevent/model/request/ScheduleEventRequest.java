package com.gucardev.springreactboilerplate.features.scheduledevent.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Map;

@Schema(description = "Schedule an event to be delivered after a delay via RabbitMQ.")
public record ScheduleEventRequest(

        @Schema(description = "Discriminator the consumer branches on to interpret the payload",
                example = "send-reminder")
        @NotBlank
        @Size(max = 100)
        String eventType,

        @Schema(description = "Optional JSON payload handled when the event fires (stored as jsonb); "
                + "omit for a type-only event",
                example = "{\"userId\":42,\"channel\":\"email\"}")
        Map<String, Object> payload,

        @Schema(description = "How long to hold the event before delivery, in seconds", example = "10")
        @NotNull
        @Min(0)
        @Max(86400) // cap at 24h so the demo can't park a message for an absurd duration
        Long delaySeconds
) {
}
