# OTP Usage

A small, reusable one-time-password primitive. It only answers one question — *"is this code valid
for this `(destination, type)` right now?"* — and performs **no side effect** itself. Any flow
(account verification, password reset, 2FA, …) composes on top of it.

Two things identify an OTP, chosen independently on every request:

- **Destination** — a phone number (for `SMS`) or email address (for `EMAIL`). The OTP is **keyed by
  destination, not by user**, so it works pre-auth and for any address.
- **Type** (`OtpType`) — the *purpose* (`ACCOUNT_VERIFICATION`, `PASSWORD_RESET`, `CHANGE_PASSWORD`,
  `LOGIN_2FA`). There is **at most one active OTP per `(destination, type)`** — sending a new one
  invalidates the previous.

> The code is **never** returned in any response. It goes out only over the chosen channel; the API
> returns delivery metadata only.

All code lives in `com.gucardev.springreactboilerplate.features.core.otp`.

---

## Endpoints

Both are public (listed in `security.ignored-paths`) because OTP flows run before the user is
authenticated. Responses use the standard `ApiResponseWrapper` envelope.

### `POST /otp/send`

```jsonc
// request
{ "destination": "+905551234567", "type": "ACCOUNT_VERIFICATION", "sendingChannel": "SMS" }

// 200 — metadata only, never the code
{ "success": true, "status": 200,
  "data": { "destination": "+905551234567", "type": "ACCOUNT_VERIFICATION",
            "sendingChannel": "SMS", "expiryTime": "2026-05-26T15:05:00" } }
```

### `POST /otp/verify`

```jsonc
// request
{ "destination": "+905551234567", "type": "ACCOUNT_VERIFICATION", "otp": "123456" }

// 200
{ "success": true, "status": 200, "message": "OTP verified" }
```

A correct code is **single-use** (burned on success). Failures throw the errors below.

---

## Configuration reference

`application.yml`:

```yaml
otp:
  length: 6                  # number of digits in a generated code
  expiry-minutes: 5          # how long a code stays valid
  max-attempts: 5            # wrong guesses before the OTP locks
  resend-cooldown-seconds: 60 # min seconds between two sends to the same (destination, type); 0 disables
  cleanup-cron: "0 0 * * * *" # hourly purge of expired/used OTPs (ShedLock-guarded)
```

Bound by `OtpProperties` (`@ConfigurationProperties("otp")`). All values have sensible defaults, so
the block is optional.

---

## Abuse protections

| Concern | Mechanism | Error (`businessErrorCode`, HTTP) |
|---|---|---|
| Resend spam / SMS-cost abuse | Cooldown between sends per `(destination, type)` | `OTP_RESEND_TOO_SOON`, 429 |
| Multiple live codes | A new send invalidates the previous active OTP | — |
| Brute-forcing the code | Lockout after `max-attempts` wrong guesses | `OTP_MAX_ATTEMPTS`, 429 |
| Stale codes | Expiry window (`expiry-minutes`) | `OTP_EXPIRED`, 410 |
| No active code / wrong code | — | `OTP_NO_ACTIVE` 404 · `OTP_INVALID_CODE` 400 |
| Table growth | `OtpCleanupJob` purges expired/used rows (one instance at a time via ShedLock) | — |

> The cooldown is **per destination**. It does not stop an attacker rotating destinations — add a
> per-IP / per-day cap at the edge if you need that.

---

## Extending it

### 1. A new purpose — add an enum value

```java
public enum OtpType {
    ACCOUNT_VERIFICATION, PASSWORD_RESET, CHANGE_PASSWORD, LOGIN_2FA,
    DELETE_ACCOUNT      // <- everything else works unchanged
}
```

### 2. A new channel or a real provider — implement `OtpSender`

The stub senders just log the code. Drop in a real provider by replacing the body, or add a whole
new channel; `OtpSenderDispatcher` wires every `OtpSender` bean by its `channel()` automatically.

```java
@Component
public class TwilioSmsOtpSender implements OtpSender {
    @Override public OtpSendingChannel channel() { return OtpSendingChannel.SMS; }
    @Override public void send(String destination, String code, OtpType type) {
        // call Twilio here
    }
}
```

(New channel value? Add it to `OtpSendingChannel`, then provide a sender for it.)

### 3. Gate a real action — compose `VerifyOtpUseCase`

The standalone `/otp/verify` endpoint just confirms-and-burns. To actually *do* something, call the
verifier **inside your action use case** so verification and the action commit in **one transaction**
— if the action fails, the burn rolls back with it:

```java
@Service
@RequiredArgsConstructor
public class ResetPasswordUseCase {

    private final VerifyOtpUseCase verifyOtpUseCase;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void execute(ResetPasswordRequest req) {
        // throws OTP_INVALID_CODE / OTP_EXPIRED / OTP_MAX_ATTEMPTS if not valid
        verifyOtpUseCase.execute(new VerifyOtpRequest(req.destination(), OtpType.PASSWORD_RESET, req.otp()));

        User user = userRepository.findByEmail(req.destination())
                .orElseThrow(() -> UserExceptionType.NOT_FOUND.toException());
        user.setPassword(passwordEncoder.encode(req.newPassword()));
    }
}
```

The same shape covers account activation (`ACCOUNT_VERIFICATION` → set `activated = true`) and 2FA
login (`LOGIN_2FA` → issue tokens after the code checks out).

> OTP is keyed by destination, not user, so action flows look the user up by destination (as above).
> If you'd rather bind an OTP to a `userId`, that's a small addition to the `Otp` entity.

---

## Files

| File | Responsibility |
|------|----------------|
| `domain/otp/entity/Otp` | The persisted OTP (`otps` table; one active per destination+type) |
| `domain/otp/enums/OtpType`, `OtpSendingChannel` | Purpose and channel enums |
| `domain/otp/config/OtpProperties` | Binds `otp.*` |
| `domain/otp/repository/OtpRepository` | Lookups + bulk invalidate/cleanup queries |
| `domain/otp/service/OtpCodeGenerator` | Numeric code generation (`SecureRandom`) |
| `domain/otp/service/sender/OtpSender` (+ `Sms`/`Email` impls, `OtpSenderDispatcher`) | Pluggable delivery |
| `domain/otp/service/usecase/SendOtpUseCase` | Cooldown check → invalidate → generate → persist → dispatch |
| `domain/otp/service/usecase/VerifyOtpUseCase` | Validate, count attempts, burn on success |
| `domain/otp/controller/OtpController` | `POST /otp/send`, `POST /otp/verify` |
| `domain/otp/scheduler/OtpCleanupJob` | Scheduled purge of expired/used OTPs (`@SchedulerLock`) |
| `domain/otp/exception/OtpExceptionType` | Error catalog (messages in `messages*.properties`) |
