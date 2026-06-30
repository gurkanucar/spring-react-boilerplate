-- Rich-domain showcase: the shopping cart aggregate.
--
-- The point of this feature is the DOMAIN MODEL, not the plumbing: all the business rules (line
-- merging, per-line / per-cart limits, coupon eligibility + discount maths, free-shipping threshold,
-- and the ACTIVE -> CHECKED_OUT lifecycle) live inside the `Cart` aggregate and its value objects.
-- The application services are thin (load -> call one behaviour -> save).
--
-- The whole aggregate is ONE row: a cart's lines have no identity or lifecycle outside their cart, so
-- they (and the optional coupon) are denormalised into `jsonb` columns and always loaded/saved with
-- the cart. Nothing priced is stored — subtotal/discount/shipping/total are always derived on read.
--
-- Column types mirror what Hibernate generates so `ddl-auto: validate` passes (see V3/V4 for the
-- rules): UUID @JdbcTypeCode(CHAR) -> character(36); audit LocalDateTime -> timestamp(6) without time
-- zone; @JdbcTypeCode(JSON) -> jsonb.

CREATE TABLE carts (
    id            character(36)               NOT NULL,
    customer_name character varying(150)      NOT NULL,
    status        character varying(20)       NOT NULL,
    lines         jsonb                       NOT NULL,
    coupon        jsonb,
    created_at    timestamp(6) without time zone,
    updated_at    timestamp(6) without time zone,
    created_by    character varying(255),
    updated_by    character varying(255),
    CONSTRAINT carts_pkey PRIMARY KEY (id)
);

CREATE INDEX idx_carts_status ON carts (status);
CREATE INDEX idx_carts_created_at ON carts (created_at);
