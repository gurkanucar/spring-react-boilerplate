-- Scheduled (delayed) events delivered via RabbitMQ. The row is the source of truth for an event's
-- lifecycle (SCHEDULED -> DELIVERED / CANCELLED / FAILED); see the ScheduledEvent entity.
-- Column types mirror what Hibernate generates so `ddl-auto: validate` passes:
--   UUID @JdbcTypeCode(CHAR) -> character(36); Instant -> timestamp(6) with time zone;
--   audit LocalDateTime -> timestamp(6) without time zone.
CREATE TABLE scheduled_events (
    id            character(36)                  NOT NULL,
    event_type    character varying(100)         NOT NULL,
    payload       jsonb,
    status        character varying(20)          NOT NULL,
    delay_seconds bigint                         NOT NULL,
    scheduled_at  timestamp(6) with time zone    NOT NULL,
    fire_at       timestamp(6) with time zone    NOT NULL,
    delivered_at  timestamp(6) with time zone,
    cancelled_at  timestamp(6) with time zone,
    attempts      integer                        NOT NULL,
    last_error    character varying(1000),
    created_at    timestamp(6) without time zone,
    updated_at    timestamp(6) without time zone,
    created_by    character varying(255),
    updated_by    character varying(255),
    CONSTRAINT scheduled_events_pkey PRIMARY KEY (id)
);

-- Query paths this table serves: by status, by fire time (overdue/upcoming), the composite
-- (status, fire_at) for a reconciliation sweep of still-SCHEDULED-but-overdue rows, and created_at
-- for time-ordered listing (the default sort).
CREATE INDEX idx_scheduled_events_status ON scheduled_events (status);
CREATE INDEX idx_scheduled_events_event_type ON scheduled_events (event_type);
CREATE INDEX idx_scheduled_events_fire_at ON scheduled_events (fire_at);
CREATE INDEX idx_scheduled_events_status_fire_at ON scheduled_events (status, fire_at);
CREATE INDEX idx_scheduled_events_created_at ON scheduled_events (created_at);

-- If you start querying *into* the jsonb payload (e.g. WHERE payload @> '{"userId":42}'), add a GIN
-- index; left out by default since the demo only reads the payload after looking the row up by id:
--   CREATE INDEX idx_scheduled_events_payload ON scheduled_events USING gin (payload);
