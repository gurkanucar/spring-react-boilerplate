package com.gucardev.springreactboilerplate.features.core.notification.adapter.in.web.dto;

import com.gucardev.springreactboilerplate.features.shared.dto.BaseFilterRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class NotificationFilterRequest extends BaseFilterRequest {

    @Schema(description = "Return only unread notifications", example = "false")
    private Boolean unreadOnly = false;
}
