package com.gucardev.springreactboilerplate.features.scheduledevent.domain.model;

/**
 * Lifecycle of a {@link ScheduledEvent}.
 *
 * <pre>
 *   SCHEDULED ──(broker releases & listener handles)──▶ DELIVERED
 *       │
 *       ├──(cancel before it fires)────────────────────▶ CANCELLED  (listener skips it on arrival)
 *       │
 *       └──(handler threw)─────────────────────────────▶ FAILED
 * </pre>
 */
public enum ScheduledEventStatus {
    SCHEDULED,
    DELIVERED,
    CANCELLED,
    FAILED
}
