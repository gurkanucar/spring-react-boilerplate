# Notifications Usage

In-app notification feed per user per workspace (`features/core/notification`), fed by events.

## Model

`Notification`: `id`, `workspaceId`, `recipientId` (the user), `type` (e.g. `NEWS_CREATED`),
`title`, `message` (`TEXT`), `read` (column `is_read`), `readAt`.

## Create a notification (event-driven, preferred)

Any feature publishes a `NotificationEvent` (in `features/shared/event`) — no dependency on the
notification module:

```java
eventPublisher.publishEvent(new NotificationEvent(workspaceId, recipientId, "NEWS_CREATED",
        "News published", news.getTitle()));
```

`NotificationEventListener` persists it via `NotificationService.create(...)`. Listeners are
synchronous, so creation joins the publisher's transaction.

## Gating

`NotificationService.create` is gated by the `IN_APP_NOTIFICATIONS` feature flag: when off for a
workspace, the notification is silently dropped. (See `feature-flags-usage.md`.)

## Endpoints

`/api/v1/notifications` (auth required, current user + active workspace):
- `GET` — paged feed (`unreadOnly` filter)
- `GET /unread-count` — `{ "count": n }`
- `PUT /{id}/read` — mark one read
- `PUT /read-all` — mark all read → `{ "updated": n }`

## Lifecycle

`WorkspaceDeletedEvent` triggers cleanup of that workspace's notifications.

## Real-time (optional)

`spring-boot-starter-websocket` is already on the classpath; push new notifications over a
STOMP/WebSocket or SSE channel from the same listener when you need live updates.
