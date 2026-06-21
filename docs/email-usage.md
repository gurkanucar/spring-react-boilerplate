# Email Usage

HTML email rendered from Thymeleaf templates and sent asynchronously (`infra/config/mail`).

## Send an email

```java
emailService.sendHtml(
    "user@example.com",
    "Your verification code",
    "otp",                                   // -> templates/email/otp.html
    Map.of("code", code, "type", type));     // Thymeleaf model
```

- Runs on the `asyncExecutor` (`@Async`) so request threads never block on SMTP.
- `EmailService.render(template, vars)` returns the HTML (handy for tests/previews).

## Templates

Live in `src/main/resources/templates/email/*.html` (plain Thymeleaf). `otp.html` ships as an example.

## Configuration

```yaml
spring.mail:            # SMTP; when host is blank no JavaMailSender is created
  host: ${MAIL_HOST:}
  port: ${MAIL_PORT:587}
  username/password: ...
app.mail:
  enabled: ${MAIL_ENABLED:false}            # master switch
  from: ${MAIL_FROM:no-reply@example.com}
```

**Boots without mail configured:** `EmailService` injects `ObjectProvider<JavaMailSender>`; if
`app.mail.enabled=false` or no SMTP host is set, it **logs and skips** instead of sending. Templates
still render identically in every environment.

## Wired example

`EmailOtpSender` (the `EMAIL` OTP channel) sends OTP codes through `EmailService`. Add new senders by
implementing `OtpSender` (see `otp-usage.md`).

## Testing

Mock `JavaMailSender` with `@MockitoBean` and assert `send(...)` is called; disable the actuator mail
health check via `management.health.mail.enabled=false` (a mock isn't a real `JavaMailSenderImpl`).
