# Transactional Outbox (RabbitMQ) Usage

Publish a domain event **reliably** when a database change commits — with no chance of "saved the row
but lost the event" or "published the event but the transaction rolled back". The trick: write the
business row **and** the event into the **same local transaction**, then let a separate relay push the
event to the broker afterwards.

This is the standard fix for the **dual-write problem**: a single request can't atomically write to a
database *and* a message broker (no shared transaction). The outbox makes the only write that must be
atomic a *single database* write.

## How it works

```
POST /api/v1/orders
   │  ONE transaction:
   │    ├─ INSERT orders            (status = PLACED)
   │    └─ INSERT outbox_messages   (status = PENDING)   ← OutboxEventRecorder
   │  commit  ──────────────────────────────────────────────► both or neither
   │
   ▼
OutboxRelayJob  (every outbox.poll-delay-ms, ONE instance via ShedLock)
   └─ OutboxDispatcher: SELECT pending ids
        └─ OutboxMessageSender  (REQUIRES_NEW per row)
             ├─ publish envelope ─► outbox.exchange (topic)   routing key = order.created
             └─ UPDATE outbox_messages SET status = PUBLISHED
                  (on failure: attempts++, backoff via next_attempt_at; FAILED after max)
                          │
                          ▼
        order.events.queue ─► OrderEventListener ─► HandleOrderCreatedUseCase
             ├─ already in processed_messages?  → skip   (idempotent)
             └─ else: confirm order + INSERT processed_messages   (same tx)

  retries exhausted on the consumer ─► order.events.dlq  (dead letter)
```

### Why each piece exists

- **`outbox_messages`** — the event, written atomically with the business change. Source of truth for
  delivery (`PENDING → PUBLISHED`, or `FAILED` after exhausting retries).
- **Relay (`OutboxRelayJob`)** — a `@Scheduled` poller, **guarded by `@SchedulerLock` (ShedLock)** so
  exactly one instance drains the table at a time across replicas. No broker I/O happens on the request
  path, so placing an order works even while RabbitMQ is down — the event just goes out once the relay
  can reach the broker.
- **Per-row `REQUIRES_NEW`** — each row publishes + updates in its own transaction, so one poison row
  can't roll back progress on its neighbours.
- **`processed_messages` (inbox)** — consumer-side idempotency. Delivery is **at-least-once** (the relay
  may re-publish after a crash; the broker may redeliver), so the consumer records each handled event id
  and skips duplicates. The marker is written **in the same tx as the side effect**, so a failure rolls
  back both and the event is retried — never half-applied.
- **DLQ** — the consumer factory retries a handful of times with backoff, then dead-letters the message
  to `order.events.dlq` instead of requeueing a poison message forever.

## Producing an event

Inject `OutboxEventRecorder` and call it inside your business `@Transactional` method, right after the
business write — see `PlaceOrderUseCase`:

```java
@Transactional
public OrderResponse execute(PlaceOrderRequest request) {
    Order order = orderRepository.save(/* ... status = PLACED ... */);

    outboxEventRecorder.record(
        "Order",                                       // aggregateType
        order.getId().toString(),                      // aggregateId
        "OrderCreated",                                // eventType (consumer branches on this)
        OutboxRabbitConfig.ORDER_CREATED_ROUTING_KEY,  // routing key on the outbox exchange
        payloadMap);                                   // jsonb body

    return orderMapper.toDto(order);
}
```

**Do not publish to RabbitMQ here.** Doing broker I/O inside the business transaction is exactly what
the outbox avoids.

## Consuming an event

Bind a `@RabbitListener` to the queue on the dedicated retry/DLQ factory, and delegate to an
idempotent, transactional handler — see `OrderEventListener` + `HandleOrderCreatedUseCase`:

```java
@RabbitListener(queues = OutboxRabbitConfig.ORDER_EVENTS_QUEUE,
                containerFactory = OutboxRabbitConfig.LISTENER_FACTORY)
public void onOrderEvent(OutboxEventEnvelope envelope) {
    handleOrderCreatedUseCase.execute(envelope);   // checks inbox, does work, records inbox — one tx
}
```

## Run it

```bash
docker compose up -d rabbitmq    # broker (management UI at http://localhost:15672, guest/guest)

# Place an order (auth required — grab a token from /auth/login first):
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"customerName":"Ada Lovelace","product":"Mechanical keyboard","quantity":2,"amount":129.90}'

# Within ~2s the relay publishes the event and the consumer confirms the order.
# Poll the order and watch status flip PLACED -> CONFIRMED:
curl http://localhost:8080/api/v1/orders/{id} -H "Authorization: Bearer $TOKEN"
```

Try it with the broker **stopped**: the order still saves (status `PLACED`, one `PENDING` outbox row).
Start RabbitMQ and the relay drains the backlog and the order flips to `CONFIRMED`.

## Configuration

`application.yml` (`outbox.*`):

| Key | Default | Meaning |
| --- | --- | --- |
| `outbox.poll-delay-ms` | `2000` | Delay between relay polls. |
| `outbox.batch-size` | `50` | Max rows claimed per poll. |
| `outbox.max-attempts` | `5` | Publish attempts before a row is marked `FAILED`. |
| `outbox.backoff-seconds` | `30` | Base backoff after a failed publish (× attempt count). |

## Scaling to multiple instances

The relay is guarded by **ShedLock** (`@SchedulerLock` in `OutboxRelayJob`), so deploying N replicas is
safe — only one runs the relay at a time, using the DB clock (`usingDbTime()`) like the other scheduled
jobs. That caps drain throughput at a single node, which is plenty for most apps.

If you need **parallel draining** across instances, drop the `@SchedulerLock` and switch the fetch in
`OutboxMessageRepository#findPublishableIds` to a locking query with **`FOR UPDATE SKIP LOCKED`**
(PostgreSQL), so every poller claims a disjoint batch concurrently. (Left out by default because
`SKIP LOCKED` isn't supported by the H2 database the test suite runs on.)

## Notes / where to extend

- **Ordering** is best-effort FIFO (oldest `created_at` first); the broker and parallel consumers don't
  guarantee strict per-aggregate order. If you need it, publish a single partition key per aggregate and
  use a single consumer per partition.
- **Confirms** — `convertAndSend` returning is treated as "broker accepted". For stronger guarantees
  enable [publisher confirms](https://docs.spring.io/spring-amqp/reference/amqp/template.html#template-confirms)
  and only mark `PUBLISHED` on the ack.
- **Purging** — published rows accumulate; add a ShedLock-guarded cleanup job (mirror
  `OtpCleanupJob`) to delete `PUBLISHED` rows older than N days, and alert on `FAILED` rows.
- **Multiple consumers** of one event — the inbox PK is the event id alone (one logical consumer per
  event). For fan-out, switch `processed_messages` to a composite key of `(message_id, consumer)`.
