# Scheduling Usage

Scheduled jobs use Spring's `@Scheduled` **plus ShedLock**, so each job runs on **one instance at a
time** even when several replicas are deployed. Without the lock, every replica would fire the same
cron job concurrently (double cleanups, duplicate emails, races).

Everything is enabled in one place — `infra/config/scheduling/SchedulerConfiguration`:

```java
@Configuration
@EnableScheduling                                    // turns on @Scheduled
@EnableSchedulerLock(defaultLockAtMostFor = "PT10M") // turns on ShedLock
public class SchedulerConfiguration implements SchedulingConfigurer {

    // Dedicated pool so jobs don't serialize on the default single-threaded scheduler,
    // plus an error handler so a thrown exception is logged, not swallowed.
    @Override public void configureTasks(ScheduledTaskRegistrar r) { r.setScheduler(taskScheduler()); }

    @Bean public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler s = new ThreadPoolTaskScheduler();
        s.setPoolSize(2);
        s.setThreadNamePrefix("Scheduler-");
        s.setErrorHandler(schedulerErrorHandler());
        s.initialize();
        return s;
    }

    @Bean public ErrorHandler schedulerErrorHandler() {
        return t -> log.error("[SCHEDULER] exception occurred", t);
    }

    @Bean public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(JdbcTemplateLockProvider.Configuration.builder()
                .withJdbcTemplate(new JdbcTemplate(dataSource))
                .usingDbTime()   // use the DB clock -> no skew between instances
                .build());
    }
}
```

> This is the single config for scheduling (`@EnableScheduling` lives here, not in
> `AsyncConfiguration`). Add a `@Scheduled` job anywhere as a Spring bean; this class is what makes
> it run (on the pool) and lock.

Locks are stored in a **`shedlock` table** on the application datasource, created by the JPA
`ShedLockEntity` (so it exists in H2 dev/test and Postgres prod under `ddl-auto`). ShedLock reads and
writes the rows itself — the entity is mapping-only.

**Why JDBC, not Redis:** the app is configured to stay up when Redis is down. Locks must always be
available, and the database always is — so ShedLock uses the datasource.

---

## Writing a job

Annotate a bean method with both `@Scheduled` (when) and `@SchedulerLock` (single-run guard):

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class MyCleanupJob {

    private final MyRepository repository;

    @Scheduled(cron = "${my.cleanup-cron}")
    @SchedulerLock(name = "MyCleanupJob_purge", lockAtMostFor = "PT5M", lockAtLeastFor = "PT10S")
    public void purge() {
        int removed = repository.deleteStaleRows(LocalDateTime.now());
        if (removed > 0) {
            log.info("Removed {} stale row(s).", removed);
        }
    }
}
```

> **Convention: every `@Scheduled` method in this project carries `@SchedulerLock`.** No extra setup
> per job — the provider and table already exist.

### `@SchedulerLock` parameters

| Parameter | Meaning | Guidance |
|---|---|---|
| `name` *(required)* | The lock row key — **must be unique per job** | Use `ClassName_method` |
| `lockAtMostFor` | Hard release if the holder dies mid-run (crash safety net) | A few × the expected runtime |
| `lockAtLeastFor` | Hold the lock at least this long, even if the job finishes instantly | Prevents a fast job double-firing on clock skew |

If `lockAtMostFor` is omitted, the `defaultLockAtMostFor` (`PT10M`) from `SchedulingConfig` applies.
Durations are ISO-8601: `PT10S` = 10s, `PT5M` = 5 min, `PT2H` = 2 hours.

How it works: at trigger time the job tries to insert/update its row in `shedlock`. If another
instance holds an unexpired lock, this run is **skipped** (not queued). When the job ends the lock is
released — but not before `lockAtLeastFor` has elapsed.

---

## Jobs in this project

| Job | Schedule (cron property) | Does |
|-----|--------------------------|------|
| `RefreshTokenCleanupJob` | `security.jwt.refresh-token-cleanup-cron` | Purges revoked/expired refresh tokens |
| `OtpCleanupJob` | `otp.cleanup-cron` | Purges expired/used OTPs |

Both use a 6-field Spring cron (`sec min hour day month weekday`), bound from `application.yml`, e.g.
`"0 0 * * * *"` (top of every hour).

---

## Notes

- **Schedules are externalized.** Jobs read their cron from a property (`@Scheduled(cron = "${...}")`)
  rather than hard-coding it, so each environment can tune it.
- **Tests.** Cron jobs fire on the clock, so they don't run during the test suite; ShedLock just
  loads its `LockProvider` bean and table at context startup.
- **Prod schema.** The `shedlock` table is created by Hibernate while `ddl-auto` is `update`. If you
  move prod to `validate`/`none` with a migration tool, create the table (and `otps`, etc.) in a
  migration.

### Files

| File | Responsibility |
|------|----------------|
| `infra/config/scheduling/SchedulerConfiguration` | `@EnableScheduling` + `@EnableSchedulerLock` + the task-scheduler pool + error handler + JDBC `LockProvider` |
| `infra/config/scheduling/ShedLockEntity` | Maps the `shedlock` table so Hibernate creates it |
| `domain/auth/scheduler/RefreshTokenCleanupJob` | Refresh-token cleanup (locked) |
| `domain/otp/scheduler/OtpCleanupJob` | OTP cleanup (locked) |
