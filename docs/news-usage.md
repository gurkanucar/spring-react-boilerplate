# News Usage

Workspace-scoped content feature (`features/news`). A reference template for a multi-tenant CRUD
feature with a slug, image/attachment references and tags.

## Model

`News` (`entity/News.java`):

| Field | Notes |
|-------|-------|
| `id` | UUID |
| `workspaceId` | tenant discriminator (indexed); unique slug is per `(workspace_id, slug)` |
| `title`, `content` | content (`content` is `TEXT`) |
| `slug` | **auto-generated** from the title (see below); not regenerated on update |
| `featured` | `Boolean` flag |
| `imageIds` | `@ElementCollection List<UUID>` (ordered), file ids |
| `featuredImageId` | must be one of `imageIds` (validated, else `400 NEWS_FEATURED_IMAGE_INVALID`) |
| `attachmentIds` | `@ElementCollection List<UUID>` (ordered), any file type |
| `tags` | `@ElementCollection Set<String>` |

Images/attachments are uploaded via the **file API** (`POST /files`, `POST /files/images`) and
referenced here by their returned UUID — same convention as the user profile image (no JPA relation).

## Endpoints

`/api/v1/news` — `GET` (paged/filtered), `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`.
Roles: `ADMIN`, `ORG_MANAGER`, `WORKSPACE_USER`. List filters: `title` (contains), `featured`, `tag`.

## Slug generation

`SlugUtil.toSlug()` (`features/shared/util`) transliterates Turkish letters, strips diacritics,
lowercases and hyphenates → matches `^[a-z0-9]+(?:-[a-z0-9]+)*$`. `CreateNewsUseCase` makes it unique
within the workspace by appending `-2`, `-3`, … Reuse `SlugUtil` for any sluggable entity.

## Tenant isolation & N+1

- `NewsFinder` enforces `workspaceId == requireWorkspaceId()`, reporting cross-workspace as `404`.
- List queries touch two `@ElementCollection`s; `hibernate.default_batch_fetch_size: 100`
  (in `application.yml`) batch-loads them to avoid N+1.

## Events

On create, `CreateNewsUseCase` publishes a `NotificationEvent` to the author (demo of the
event-driven flow; the notification module gates it on the workspace flag).
