-- Transactional outbox pattern demo backed by RabbitMQ.
--
-- The point of the pattern: a business write (insert into `orders`) and the intent to publish an
-- event (insert into `outbox_messages`) happen in the SAME local transaction, so they are atomic —
-- there is no window where the order is saved but the event is lost, or vice versa. A separate
-- relay (a ShedLock-guarded scheduled poller) later reads PENDING rows and publishes them to the
-- broker, marking them PUBLISHED. The consumer dedupes via `processed_messages` (the "inbox") so
-- at-least-once delivery + relay retries stay safe.
--
-- Column types mirror what Hibernate generates so `ddl-auto: validate` passes (see V3 for the rules):
--   UUID @JdbcTypeCode(CHAR) -> character(36); Instant -> timestamp(6) with time zone;
--   audit LocalDateTime -> timestamp(6) without time zone; BigDecimal(12,2) -> numeric(12, 2).

-- ---------------------------------------------------------------------------------------------------
-- Business aggregate the demo writes. Status starts PLACED and flips to CONFIRMED once the consumer
-- handles the OrderCreated event (proving the event made it all the way through end-to-end).
-- ---------------------------------------------------------------------------------------------------
CREATE TABLE orders (
    id            character(36)               NOT NULL,
    customer_name character varying(150)      NOT NULL,
    product       character varying(150)      NOT NULL,
    quantity      integer                     NOT NULL,
    amount        numeric(12, 2)              NOT NULL,
    status        character varying(20)       NOT NULL,
    created_at    timestamp(6) without time zone,
    updated_at    timestamp(6) without time zone,
    created_by    character varying(255),
    updated_by    character varying(255),
    CONSTRAINT orders_pkey PRIMARY KEY (id)
);

CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_orders_created_at ON orders (created_at);

-- ---------------------------------------------------------------------------------------------------
-- The outbox. One row per event the app intends to publish, written in the same tx as the business
-- change. `status` drives the relay; `next_attempt_at` gates retry backoff after a failed publish;
-- `attempts`/`last_error` are bookkeeping. `routing_key` lets the generic relay publish each row to
-- the right binding on the shared outbox exchange without knowing the event's type.
-- ---------------------------------------------------------------------------------------------------
CREATE TABLE outbox_messages (
    id              character(36)               NOT NULL,
    aggregate_type  character varying(100)      NOT NULL,
    aggregate_id    character varying(100)      NOT NULL,
    event_type      character varying(100)      NOT NULL,
    routing_key     character varying(150)      NOT NULL,
    payload         jsonb,
    status          character varying(20)       NOT NULL,
    attempts        integer                     NOT NULL,
    last_error      character varying(1000),
    next_attempt_at timestamp(6) with time zone,
    published_at    timestamp(6) with time zone,
    created_at      timestamp(6) without time zone,
    updated_at      timestamp(6) without time zone,
    created_by      character varying(255),
    updated_by      character varying(255),
    CONSTRAINT outbox_messages_pkey PRIMARY KEY (id)
);

-- The relay's hot path: "PENDING rows whose next_attempt_at is due, oldest first". The partial-ish
-- composite (status, next_attempt_at) covers it; created_at keeps the FIFO ordering cheap.
CREATE INDEX idx_outbox_status_next_attempt ON outbox_messages (status, next_attempt_at);
CREATE INDEX idx_outbox_status ON outbox_messages (status);
CREATE INDEX idx_outbox_created_at ON outbox_messages (created_at);

-- ---------------------------------------------------------------------------------------------------
-- The inbox / idempotency ledger. The consumer records each event id it has fully processed; a
-- redelivery (inherent to at-least-once) finds the row and is skipped. message_id is the outbox id
-- carried in the envelope. `consumer` is informational here (one logical consumer per event in this
-- demo) — switch the PK to a composite (message_id, consumer) if you fan an event out to several.
-- ---------------------------------------------------------------------------------------------------
CREATE TABLE processed_messages (
    message_id   character(36)               NOT NULL,
    consumer     character varying(150)      NOT NULL,
    processed_at timestamp(6) with time zone NOT NULL,
    CONSTRAINT processed_messages_pkey PRIMARY KEY (message_id)
);
