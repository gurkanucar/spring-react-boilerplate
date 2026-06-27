# RabbitMQ Scheduled (Delayed) Events Usage

Publish an event **now** and have RabbitMQ deliver it to a consumer **after a delay** — no DB polling
and no in-app timer. The broker holds each message until its delay elapses, then routes it to the
queue like normal. Useful for "send a reminder in 10 minutes", "expire this reservation in 1 hour",
"retry this webhook in 30 seconds", etc.

This is **per-message scheduling**: every message carries its own delay, so one queue can hold events
firing seconds or hours apart.

Each scheduled event is **persisted** as a `scheduled_events` row — the source of truth for its
payload and lifecycle. That makes the work **cancellable** and **auditable** even though the broker
itself can't recall an already-published delayed message.

## How it works

It relies on the broker's [`rabbitmq_delayed_message_exchange`](https://github.com/rabbitmq/rabbitmq-delayed-message-exchange)
plugin (not bundled with the stock image — we bake it into our image, see `docker/rabbitmq/Dockerfile`):

1. Persist a `ScheduledEvent` row (`status = SCHEDULED`).
2. After the tx commits, publish to the `x-delayed-message` exchange with an **`x-delay` header** (ms).
3. The broker parks the message, then routes it by routing key to the bound queue when the delay is up.
4. A `@RabbitListener` consumes it, loads the row by id, and drives it to `DELIVERED` / `FAILED`
   (or skips it if it was `CANCELLED`).

```
POST /api/v1/scheduled-events
   │  save row (SCHEDULED) ─▶ scheduled_events
   └─ after commit ─▶ ScheduledEventPublisher (x-delay = delaySeconds*1000)
                          │
                          ▼
        scheduled.exchange (x-delayed-message)  ...broker holds for the delay...
                          │
                          ▼
        scheduled.events.queue ─▶ ScheduledEventListener ─▶ load row, run, mark DELIVERED/FAILED
```

### Status lifecycle

```
SCHEDULED ──(broker releases & listener handles)──▶ DELIVERED
    │
    ├──(DELETE before it fires)────────────────────▶ CANCELLED   (listener skips it on arrival)
    └──(handler threw)─────────────────────────────▶ FAILED
```

## The event shape

- **`eventType`** (string) — a discriminator. The consumer switches on it to know how to read the
  payload (which keys to expect, what to cast). See the `switch` in `ProcessScheduledEventUseCase`.
- **`payload`** (`jsonb`, **optional**) — arbitrary structured JSON. Stored in a Postgres `jsonb`
  column via Hibernate's JSON mapping, so any shape works and you can later query into it
  (`WHERE payload @> '{"userId":42}'` — add the GIN index noted in the migration first). Omit it for
  a **type-only event** whose `eventType` already fully describes the action (e.g. `daily-rollup`).

## Run it

```bash
# Start the broker (built locally so the delayed-message plugin is enabled).
docker compose up -d rabbitmq
# Management UI: http://localhost:15672  (guest / guest)

# Schedule an event 10s out (endpoint requires auth — send your JWT):
curl -X POST http://localhost:8080/api/v1/scheduled-events \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"eventType":"send-reminder","payload":{"userId":42,"channel":"email"},"delaySeconds":10}'
```

Response (`202 Accepted`):

```json
{
  "data": {
    "id": "f3c1...",
    "eventType": "send-reminder",
    "payload": { "userId": 42, "channel": "email" },
    "status": "SCHEDULED",
    "delaySeconds": 10,
    "scheduledAt": "2026-06-26T09:00:00Z",
    "fireAt": "2026-06-26T09:00:10Z",
    "deliveredAt": null,
    "cancelledAt": null,
    "attempts": 0
  }
}
```

~10s later the listener handles it and the row flips to `DELIVERED`:

```
[SCHEDULED-EVENT] received id=f3c1... type=send-reminder fireAt=...
[SCHEDULED-EVENT] handling id=f3c1... type=send-reminder payload={userId=42, channel=email} ...
[SCHEDULED-EVENT] -> send reminder to userId=42 via email
[SCHEDULED-EVENT] delivered id=f3c1... attempts=1
```

### Other endpoints

```bash
GET    /api/v1/scheduled-events/{id}        # details + current status
GET    /api/v1/scheduled-events?status=SCHEDULED   # paged list, optional status filter
DELETE /api/v1/scheduled-events/{id}        # cancel a still-pending event (409 if not SCHEDULED)
```

**Cancelling** flips the row to `CANCELLED`. The broker still delivers the in-flight message when the
delay is up, but `ProcessScheduledEventUseCase` sees the status and skips the work — this is how you
"cancel" a delayed-message-plugin event (the plugin has no API to recall a published message).

## Add your own event type

1. `POST` with a new `eventType` + whatever `payload` keys it needs.
2. Add a `case "<your-type>"` to the `switch` in `ProcessScheduledEventUseCase.handle(...)`, reading
   the keys you expect out of the `payload` map.

No new queue/exchange needed — every type flows through the same queue and is dispatched by
`eventType` on the consumer side.

## Min / max delay

`x-delay` is a **32-bit signed int of milliseconds**, so the plugin's ceiling is ~**24.85 days**
(`Integer.MAX_VALUE` ms); larger values overflow and tend to deliver immediately. Minimum is `0`
(immediate). The request DTO caps `delaySeconds` at `86400` (24h) as a demo guardrail — raise it up to
the plugin limit if you need to.

## Notes

- **At-least-once delivery.** A message can be redelivered. The handler is idempotent via status:
  an already-`DELIVERED` row is skipped, and `attempts` is tracked on the row.
- **Failures.** A throwing handler records `FAILED` + `lastError` and **acks** (no rethrow) so a
  poison message can't loop forever in this demo. A real app would configure listener retry/backoff
  and a dead-letter queue instead.
- **Publish-after-commit.** The message is published in an `afterCommit` hook, so a small/zero delay
  can't let the listener read the row before the insert is visible; a rolled-back tx sends nothing.
- **Lazy connection.** The app boots even if the broker is down; the listener container just retries.
  Publishing while the broker is down fails at call time.
- **Restart durability.** Exchange/queue/bindings are durable and a parked `x-delayed` message is held
  inside the broker, so it survives a broker restart — but **not** `docker compose down -v` (the
  `rabbitmqdata` volume is wiped). The `scheduled_events` row survives regardless, so a reconciliation
  sweep over `(status, fire_at)` could re-publish events orphaned by a broker wipe.
- **Plugin version.** `docker/rabbitmq/Dockerfile` pins the plugin `.ez` to the broker's major version
  (`4.1.x`). If you bump the `rabbitmq` image, bump `PLUGIN_VERSION` to match.
- **Tests.** No broker runs in the suite — `spring.rabbitmq.listener.simple.auto-startup=false` keeps
  listeners from spinning on a connection. `ScheduledEventPublisherTest` covers the `x-delay` header;
  `ScheduledEventJsonPersistenceIT` covers the `jsonb` payload round-trip.
- **Alternative without the plugin.** A TTL + Dead-Letter-Exchange (DLX) setup gives delays on the
  stock image, but per-message TTL suffers head-of-line blocking, so fixed-delay "bucket" queues are
  the clean DLX shape. The plugin is used here because it supports arbitrary per-message delays.

### Files

| File | Responsibility |
|------|----------------|
| `docker/rabbitmq/Dockerfile` | RabbitMQ image with the `rabbitmq_delayed_message_exchange` plugin enabled |
| `docker-compose.yml` (`rabbitmq`) | Builds/runs the broker; ports `5672` (AMQP) + `15672` (UI) |
| `db/migration/V3__scheduled_events.sql` | `scheduled_events` table (jsonb payload) + indexes |
| `infra/config/rabbitmq/RabbitMqConfig` | Declares the delayed exchange, queue, binding + JSON converter; holds the names/`x-delay` constant |
| `features/scheduledevent/entity/ScheduledEvent` | Persisted event: `eventType`, `jsonb` payload, status, timestamps, attempts |
| `features/scheduledevent/publisher/ScheduledEventPublisher` | Publishes with the `x-delay` header |
| `features/scheduledevent/listener/ScheduledEventListener` | `@RabbitListener`; delegates to the process use case |
| `features/scheduledevent/service/usecase/*` | Schedule / Process / Cancel / Get / List use cases (+ Finder) |
| `features/scheduledevent/controller/ScheduledEventController` | `POST` / `GET {id}` / `GET` (list) / `DELETE {id}` |
| `features/scheduledevent/model/...` | Request, response DTO, filter, and the on-the-wire `ScheduledEventMessage` |
