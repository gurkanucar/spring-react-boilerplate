package com.gucardev.springreactboilerplate.features.scheduledevent.adapter.in.web.dto;

import com.gucardev.springreactboilerplate.features.scheduledevent.domain.model.ScheduledEventStatus;
import com.gucardev.springreactboilerplate.features.shared.dto.BaseFilterRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Adds a status filter on top of the shared paging/sorting/date filters.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ScheduledEventFilterRequest extends BaseFilterRequest {

    @Schema(description = "Filter by lifecycle status", example = "SCHEDULED")
    private ScheduledEventStatus status;
}
