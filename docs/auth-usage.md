# Auth Usage

Stateless JWT authentication. Clients log in once, then send a **bearer access token** on every
request; a longer-lived **refresh token** mints new access tokens without re-entering credentials.

Two token types:

- **Access token** — a signed JWT (HS256). Subject = the user's email; carries `userId` + `roles`
  claims. Short-lived (`token-validity-in-minutes`). Stateless: never stored server-side.
- **Refresh token** — an opaque, random value **persisted** in `refresh_tokens` (so it can be
  revoked). **Rotated** on every refresh: the presented one is revoked and a new one issued.

> Authorization always reloads the user from the database on each request (via the JWT filter), so
> role/enabled changes take effect immediately — the token claims are informational, never trusted
> for access decisions.

Auth code lives in `com.gucardev.springreactboilerplate.domain.auth`; the security plumbing in
`infra.config.security`.

---

## Endpoints

`register / login / refresh / logout` are public (in `security.ignored-paths`); `me` requires a
valid token. Responses use the standard `ApiResponseWrapper` envelope.

| Endpoint | Auth | Body | Returns |
|---|---|---|---|
| `POST /auth/register` | public | email, password, name, surname?, phoneNumber? | `TokenResponseDto` (auto-login) |
| `POST /auth/login` | public | email, password | `TokenResponseDto` |
| `POST /auth/refresh` | public | refreshToken | `TokenResponseDto` (rotated) |
| `POST /auth/logout` | public | refreshToken | 200 — revokes that refresh token |
| `GET /auth/me` | **bearer** | — | `UserResponseDto` |

```jsonc
// POST /auth/login  ->  200
{ "success": true, "status": 200,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "0f8c2b1a-...",
    "tokenType": "Bearer",
    "expiresIn": 48000,                       // access-token lifetime, seconds
    "user": { "id": "…", "email": "admin@mail.com", "roles": ["ADMIN"], "isActive": true }
  } }
```

Authenticated calls send the access token:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

When the access token expires, call `POST /auth/refresh` with the refresh token to get a fresh pair;
the old refresh token is invalidated (rotation). `POST /auth/logout` revokes a refresh token.

---

## Roles & authorization

Authorization is annotation-driven (`@EnableMethodSecurity`). Guard a handler with `@PreAuthorize`:

```java
@PreAuthorize("hasRole('ADMIN')")            // single role
@PreAuthorize("hasAnyRole('ADMIN','USER')")  // either
@PreAuthorize("isAuthenticated()")           // any logged-in user
```

> **Role names are stored without the `ROLE_` prefix** (`"ADMIN"`, `"USER"` in the `roles` table and
> in API responses). The `ROLE_` prefix is a Spring Security internal: `UserPrincipal` adds it when
> building authorities, and `hasRole('ADMIN')` re-adds it when checking — so the two line up. Never
> store or return `ROLE_`-prefixed strings.

`USER` and `ADMIN` roles plus a dev admin (`admin@mail.com` / `pass`) are seeded by `DataSeeder`.
Admin CRUD for users and roles lives at `/api/v1/users` and `/api/v1/roles` (both `hasRole('ADMIN')`).

---

## Protecting endpoints

Two independent layers:

1. **URL allow-list** — `security.ignored-paths` (in `application.yml`) lists paths reachable
   **without authentication** (Swagger, `/auth/login`, `/otp/*`, `/public/**`, actuator, …). Anything
   not listed requires a valid token (`anyRequest().authenticated()` → `401` envelope otherwise).
2. **Method rules** — `@PreAuthorize` on the handler for role checks (`403` when the user lacks the
   role).

So: not authenticated → **401**; authenticated but wrong role → **403**.

---

## Account state

- `isActive` — the enabled/disabled switch. A disabled account can't log in (`AuthenticationManager`
  raises `DisabledException` → 401) and the JWT filter won't authenticate it.
- `activated` — an email-verification flag, kept on the user but **not enforced at login**. Wire it to
  an OTP `ACCOUNT_VERIFICATION` flow if you want to require verification (see `docs/otp-usage.md`).

---

## Configuration reference

`application.yml`:

```yaml
security:
  jwt:
    # 32+ byte HMAC secret. A dev/test default lets the app boot; ALWAYS override in real envs.
    secret-key: ${JWT_SECRET_KEY:dev-only-change-me-…}
    token-validity-in-minutes: 800
    refresh-token-validity-in-minutes: 2400
    refresh-token-cleanup-cron: "0 0 * * * *"   # see docs/scheduling-usage.md
  ignored-paths: >
    /auth/register, /auth/login, /auth/refresh, /auth/logout,
    /otp/send, /otp/verify, /public/**, /actuator/**, /v3/api-docs/**, ...
```

Bound by `JwtProperties` (`@ConfigurationProperties("security.jwt")`).

---

## Errors

| `businessErrorCode` | HTTP | When |
|---|---|---|
| `EMAIL_ALREADY_EXISTS` | 409 | Register with a taken email |
| `AUTHENTICATION_FAILED` | 401 | Wrong email/password (`BadCredentialsException`) |
| `ACCOUNT_DISABLED` | 401 | Login to an `isActive = false` account |
| `INVALID_REFRESH_TOKEN` | 401 | Refresh/logout with an unknown or revoked token |
| `REFRESH_TOKEN_EXPIRED` | 401 | Refresh with an expired token |
| `AUTHENTICATION_REQUIRED` | 401 | Protected endpoint, no/invalid bearer token |
| `ACCESS_DENIED` | 403 | Authenticated but missing the required role |

---

## How it fits together

```
POST /auth/login ─► AuthenticationManager (DaoAuthenticationProvider)
                     └─ CustomUserDetailsService.loadUserByUsername(email) ─► UserPrincipal
                  ─► AuthTokenService: JwtService.generateAccessToken + persist RefreshToken
                  ─► TokenResponseDto

GET /auth/me ─► JwtAuthenticationFilter: read "Authorization: Bearer", validate, reload user,
                populate SecurityContext ─► controller ─► @PreAuthorize ─► handler
```

### Files

| File | Responsibility |
|------|----------------|
| `domain/auth/controller/AuthController` | `/auth/register|login|refresh|logout|me` |
| `domain/auth/service/usecase/*UseCase` | Register, Login, RefreshToken, Logout, GetCurrentUser |
| `domain/auth/service/AuthTokenService` | Mints access JWT + persists/rotates refresh token |
| `domain/auth/entity/RefreshToken` (+ repository) | Persisted, revocable refresh tokens |
| `infra/config/security/SecurityConfig` | Filter chain, CORS, `AuthenticationManager`, wires the JWT filter |
| `infra/config/security/jwt/JwtService` | Generate/validate/parse access tokens (JJWT, HS256) |
| `infra/config/security/jwt/JwtAuthenticationFilter` | Reads the bearer token, authenticates the request |
| `infra/config/security/jwt/CustomUserDetailsService` + `UserPrincipal` | Loads the user, builds authorities (`ROLE_` prefix) |
| `infra/config/security/jwt/JwtProperties` | Binds `security.jwt.*` |
| `infra/seed/DataSeeder` | Seeds `USER`/`ADMIN` roles + dev admin |
