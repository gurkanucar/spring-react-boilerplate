# Backend Olmazsa Olmazlar — Kapsamlı Referans

İki bölüm: **Part I** orta ölçekli her projede *default ne eklenir / neye dikkat edilir*
checklist'i; **Part II** bu prensiplerin uçtan uca uygulandığı *örnek case'ler* (outbox +
RabbitMQ, asenkron e-posta, JobRunr'sız versiyon) + kısa bir *pattern sözlüğü*.

Bağlam: Spring Boot varsayıldı ama prensipler geneldir.

---

# PART I — BEST PRACTICES

## 0. Gün 1 — yeni projede hemen kurulanlar

- [ ] Katmanlı yapı: `controller → service → repository`, iş mantığı **service**'te
- [ ] DTO katmanı (entity asla client'a dönülmez)
- [ ] Global exception handler + standart `success/error` response zarfı
- [ ] Request validation (Bean Validation)
- [ ] Auth iskeleti (JWT/OAuth2) + refresh token
- [ ] Yapılandırılmış loglama + **trace id** (MDC)
- [ ] DB migration aracı (Flyway/Liquibase)
- [ ] Connection pool (HikariCP) ayarı
- [ ] Ortam ayrımı: `dev / test / uat / prod`
- [ ] Actuator: health (liveness/readiness) + metrics (Micrometer)
- [ ] `.editorconfig` + formatter + linter + (varsa) SonarQube
- [ ] Tüm dış çağrılarda **timeout**
- [ ] Rate limiting (en azından kötüye kullanım koruması)
- [ ] Semver (`x.y.z`) + Conventional Commits

---

## 1. Proje iskeleti & katmanlar

**Default**
- İş mantığı **service layer**'da; controller ince (sadece HTTP ↔ DTO çevirisi).
- Entity → DTO her zaman; entity'ler dışarı sızmaz.
- Tutarlı cevap zarfı: her endpoint `{ success, data, error }`.

**Dikkat**
- Sınıf > 500 satır, method > 50 satır, method > 3 parametre olmasın (fazlaysa DTO).
- Guard clause (early return); iç içe `if` yerine koşullu method'lara böl.
- *Beauty is in simplicity* — sadelik azaltmayla gelir, gereksiz soyutlama ekleme.

---

## 2. API tasarımı

**Default**
- Tutarlı REST kuralları; versiyonlama (`/api/v1`) baştan.
- **Pagination her listede** (>50 kayıt kesin); response'ta sayfa metadata'sı (`page, size, total`).
- Search + filter: Spring Data `Specification` / QueryDSL ile dinamik.
- Request validation eksiksiz: `@NotNull`, `@NotBlank`, `@Size`, `@Pattern`, `@Email` + custom.

**Dikkat**
- Performans-kritik listede offset yerine **cursor/slice-based** (id veya created_at) — derin
  offset yavaştır; `count(*)` maliyetini de unutma.
- Sort/filter parametrelerini **sanitize** et (sorgu manipülasyonu/injection).
- **Idempotency key**'li POST: tekrar gönderimde çift kayıt olmasın (ödeme/sipariş gibi kritik
  yazmalarda `Idempotency-Key` header'ı + dedup).
- Birden çok kayıt için **batch API**; istemciyi döngüde tek tek çağırmaya zorlama.
- Geri döndürülemeyen işlemlerde (silme, toplu güncelleme) onay/iki-adım.

### Global Exception Handler
`@RestControllerAdvice` ile merkezi. Sunucu içi hatalar (500) istemciye detaysız döner, loglanır.

```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Email formatı geçersiz",
    "timestamp": "2026-06-30T12:00:00Z",
    "traceId": "abc-123"
  }
}
```

- Validation, 400/401/403/404/409/500 → hepsi handler'da, standart formatta.
- Hata **kodu** kataloğu tut (i18n ve frontend için sabit `code` alanı).

---

## 3. Persistence, ORM & sorgu performansı

**Default**
- İlişkiler net: One-to-Many / Many-to-Many; **owning side** bilinçli seçilsin.
    - One-to-Many: `@OneToMany(mappedBy=...)` + `@ManyToOne`.
    - Many-to-Many: mümkünse `@JoinTable` yerine **ara tablo entity'si** (ek alan eklenebilir olsun).
- Migration ile şema; elle SQL sonrası PK/FK/unique/index doğrula.
- Connection pool (HikariCP) doğru boyutlandır.

**Dikkat — sahada acıtanlar**
- **N+1**: ilişkiyi tek seferde çek (`join fetch`, `@EntityGraph`). Pagination + fetch join'de
  dikkat — gerekirse iki sorgu ya da `@EntityGraph`.
- Aggregate (COUNT/SUM/AVG) **DB'de** çalışsın, Java stream'inde değil.
- Ağır sorgu/rapor: **önce native SQL yaz, sonra ORM'e taşı**; **projection** (entity değil,
  sadece gereken kolonlar). `SELECT *`'tan kaçın.
- Bulk işlemde tek tek `save` yerine **bulk operations**.
- Concurrency: **optimistic locking** (`@Version`) ya da **unique constraint**.
- **Soft delete** (mümkünse) + kritik veride **audit trail** (`@CreatedDate/@LastModifiedDate`,
  Envers).
- Null/boş kontrolü yapmadan işleme girme.
- Java `List.of()` / `.toList()` **immutable** döner — üzerine ekleyeceksen kopyala (Unmodifiable hatası).

---

## 4. Authentication & Authorization

**Default**
- JWT veya OAuth2; yetki endpoint/method seviyesinde (RBAC veya permission-based).
- Token'da minimum claim (`sub, roles, iat, exp`); **refresh token** akışı.

**Dikkat**
- JWT varsayılan **imzalıdır (integrity), şifreli DEĞİL** — içeriği herkes okuyabilir. Token'a
  parola/sır koyma; gerçek gizlilik gerekiyorsa JWE.
- Scheduled/async işlemlerde **auth context yoktur** — gereken kullanıcıyı job'a parametre
  olarak taşı, ThreadLocal'a güvenme (system user context'i bilinçli kur).

---

## 5. Güvenlik & sağlamlık

**Default**
- Tüm girdilerde validation + **sanitization** (XSS/injection).
- **Rate limiting** — *iş kuralı limiti* (kalıcı/DB) ile *altyapı koruması*nı (in-memory/
  Resilience4j/Bucket4j/gateway) ayır.
- Secret/parola için **vault** (Vault, AWS Secrets Manager); DB'de plaintext değil. Parolalar
  hash'li (bcrypt/argon2).

**Dikkat**
- File upload'ta **magic bytes** (Apache Tika) — uzantıya güvenme.
- SSL hem server hem client tarafında doğrulansın.
- Origin IP koruması (örn. Cloudflare proxy arkasında); origin sızmasın.
- CORS ve güvenlik header'ları (HSTS, X-Content-Type-Options vb.) bilinçli ayarlansın.
- DB kullanıcısı **least privilege** (uygulama kullanıcısı DDL yetkisi taşımak zorunda değil).

---

## 6. Async, Scheduler & Message Broker

**Default**
- Uzun süren her iş **asenkron** (`202 Accepted` + durum sorgulama; HTTP thread'ini bekletme).
- Scheduler: multi-instance'ta tek-çalışma için **ShedLock** (veya JobRunr gibi DB-tabanlı job
  sistemi).
- DB yazma + mesaj yayını birlikteyse: **OUTBOX**. Tüketicide tekrara karşı **inbox / idempotency**.

**Dikkat**
- **Broker şart değil**: tüketici aynı uygulamadaysa scheduler/job sistemi çoğu zaman yeter
  ("Kafka yerine scheduler" gerçek bir seçenek).
- Kafka kullanıyorsan:
    - **Manuel ack**, `auto.commit=false` (`ack-mode=manual_immediate`).
    - Sıfırdan okunacaksa `earliest`, devam ediyorsa `latest`.
    - Uzun işlemlerde `session.timeout` iyi ayarla; consumer içinde işi **async**'e devret (rebalance
      tetiklenmesin).
    - Trace id'yi mesaja taşı (Micrometer baggage / header).
- Idempotency: aynı mesaj iki kez işlenince çift iş yapma (unique key / işlendi defteri).
- Ard arda tetiklenen üretim istekleri (rapor vs.) dedup/rate-limit ile handle edilmeli.

> Detaylı uygulama için bkz. **Part II** — Case A/B/C.

---

## 7. Caching (Redis)

**Default**
- Sık okunan, az değişen veri için cache; net **TTL** + **invalidation** stratejisi.
- Geçici/TTL'li veri (OTP, session, rate-limit sayaç) için Redis ideal: **key TTL'i hem süreyi
  hem temizliği yönetir** — süre dolunca kayıt kendiliğinden silinir, ayrı cleanup job gerekmez.
  Distributed lock için Redisson. OTP'nin DB vs Redis tasarımı için bkz. **Part II — Case D**.

**Dikkat**
- Cache invalidation en zor problemdir — yazma yollarında cache'i güncelle/temizle.
- Cache stampede ve büyük key'lere dikkat.

---

## 8. Dosya işlemleri & dış servisler

**Default**
- S3 ile yükleme/indirme; büyük dosyada **pre-signed URL** (dosya uygulamadan geçmesin).
- Dosya indir/yükle için **tek API**, içinde **business logic yok** (saf transfer); büyük dosyada
  multipart/chunked.
- Dış servis: Feign/WebClient soyutlaması + **timeout** + retry + circuit breaker + fallback.

**Dikkat**
- Stream işlemlerinde **kapanıyor mu** (try-with-resources).
- Döngüde servis çağırma; **batch/bulk** tercih et.
- Tüm HTTP client'larda timeout **zorunlu** (yoksa thread havuzu kilitlenir).

---

## 9. Observability — loglama, trace, metrics

**Default**
- Yapılandırılmış (tercihen JSON) loglama; seviyeler doğru (`trace<debug<info<warn<error`).
- **Trace id her istekte** (MDC), log'lara ve broker mesajlarına taşınır.
- Metrics + alerting (Micrometer → Prometheus/Grafana); prod'da hata izleme (Sentry vb.).
- Health probe'ları: liveness vs readiness ayrı.

**Dikkat — saha notları**
- *LOG LOG LOG* — ama **her şeyi loglama**: büyük datayı (dosya içeriği, uzun liste) loglarsan
  sistemi boğarsın.
- **Async'te trace id**: `TaskDecorator` ile MDC taşı, **iş bitince clear et** (thread pool'da
  context sızar).
- Log şişmesini önle (seviye + sampling). CPU/MEM izle (htop/btop/glances); N+1 ve ağır sorguları
  log'dan yakala.

---

## 10. Konfigürasyon & Deployment

**Default**
- Runtime-ayarlanabilir config DB'de tutulabilir (feature flag, eşikler). *Ama:* bootstrap
  config (DB bağlantısı) ve **secret'lar DB'de değil — vault'ta**.
- Ortamlar `dev/test/uat/prod`; alt ortamlar prod'a olabildiğince benzesin.
- **Graceful shutdown** + blue/green (ya da rolling) deployment.
- 12-factor: config ortamdan, stateless servis, log stdout'a.

**Versiyonlama — Semver (`x.y.z`)**
- `MAJOR.MINOR.PATCH`:
    - **MAJOR (x):** geriye uyumsuz API değişiklikleri
    - **MINOR (y):** geriye uyumlu yeni özellik
    - **PATCH (z):** geriye uyumlu bug fix
- Build/pre-release gerekiyorsa semver eki kullan: `1.4.2+build.57`, `2.0.0-rc.1` (çekirdek yine 3'lü).
- **Conventional Commits** ile sürümü otomatikleştir (`feat→minor`, `fix→patch`, `!/BREAKING→major`).

**Dikkat**
- **Zaman/timezone**: app, server, Docker, DB connection timezone'ları **hizalı** (UTC sakla,
  sınırda çevir). En sinsi bug kaynaklarından.
- Rolling/blue-green ile **geriye uyumlu migration**: önce additive (kolon ekle), kod deploy,
  sonra temizlik — eski ve yeni sürüm aynı şemada birlikte çalışabilmeli.
- SQLite kullanıyorsan WAL dahil düzenli yedek (scheduler).
- Migration sonrası PK/FK/unique/index doğrula.

---

## 11. Testing & kod kalitesi

**Default**
- **Entegrasyon testi öncelikli** (gerçek davranışı doğrular); unit test tamamlayıcı.
- Controller için `@WebMvcTest`; gerçek DB/Kafka/Redis için **Testcontainers**.
- SonarQube + formatter + linter CI'da.
- SMTP testleri için **MailHog / Mailpit**.

**Dikkat**
- Kritik akışlarda özel test: outbox atomikliği ("rollback'te job kaydedilmedi mi?"),
  idempotency (aynı mesaj iki kez → tek sonuç), concurrency (yarış durumu).

---

## 12. Frontend notları

- **Axios instance + interceptors**: token yönetimi, hata handling, loading state merkezi.
- **State**: Zustand (hafif) — store'lar domain bazlı ayrılır.
- **Server state**: React Query (TanStack) — fetch/cache/refetch/invalidation/optimistic update.
- **UI**: bir component library (Ant Design vb.).
- API katmanı soyutlanır; error/success/loading UI'da tutarlı gösterilir.

---

## 13. Tasarım prensipleri & ekip kültürü

**Tasarım**
- Sistemi **kullanıcı gözünden** tasarla; sadelik = azaltma.
- Geri döndürülemeyen işlemde acele etme — vakit iste, alternatif düşün.
- Domain bilgisi teknolojiden az önemli değil; problemi anlamadan kod yazma.

**Ekip & kariyer** (teknik dışı, bilinçli korundu)
- Her statü/karar **yazılı ve belgeli** (sorumluluk netliği).
- Bilgi edinilmeden iş %50 bitmemiş sayılır — önce öğren.
- Hata anında ilk refleks suçlu aramak değil, **ekibi desteklemek**.
- Debugging'de alçakgönüllülük: bazen sorun kodda değil — kabloyu/USB portunu değiştirip dene.
- Talepkar ol: hedeflerini (terfi/iç kadro) yöneticine açık ve sürekli dillendir.
- *Practice makes perfect*; imkansızı başarmanın yolu çoğu zaman **şartları değiştirmektir**.

---

## Hızlı "neye dikkat" hafıza kartı

| Alan | En sık unutulan |
|------|-----------------|
| ORM | N+1, projection, bulk, optimistic locking |
| API | cursor pagination, batch, idempotency-key, response zarfı |
| Async | outbox + idempotency, ShedLock, manuel ack |
| Log | trace id + MDC clear, aşırı log şişmesi |
| Güvenlik | magic bytes, sanitization, vault, timeout, least privilege |
| Zaman | UTC + timezone hizası (app/server/docker/db) |
| Deploy | graceful shutdown, geriye-uyumlu migration, semver + conventional commits |

---

# PART II — ÖRNEK CASE'LER

Aşağıdaki üç case, Part I'deki prensiplerin (outbox, idempotency, async, scheduler) birlikte
nasıl çalıştığını gösterir. Tam kodlar ayrı README'lerde; burada **özü + kilit karar**.

## Case A — Outbox + RabbitMQ + JobRunr (yoklama → rapor → dış servis)

**Problem:** Ders kapanınca (manuel veya scheduler) güvenilir biçimde rapor üret + dış servise
gönder. DB yazma ile event yayını **atomik** olmalı.

```
[Hoca / Scheduler] → closeLesson() @Transactional
        → DB: lesson + outbox (TEK transaction)
        → Relay (poll) → RabbitMQ (+DLQ)
        → Consumer → ReportService (enrolled−present = absent) → report
        → dış servis çağrısı (idempotency key + retry)
```

**Kilit kararlar**
- **Outbox**: ders state'i ve `LessonClosed` event'i aynı `@Transactional`'da → dual-write çözülür.
- **Event ince**: sadece `lessonId` taşı; consumer veriyi DB'den çeker (her zaman güncel).
- **Idempotency**: `reports.lesson_id` UNIQUE + consumer `existsByLessonId` → çift rapor yok.
- **Manuel ack**: iş bitmeden ack'leme; çökerse mesaj geri gelir.

```java
@Transactional
public void closeLesson(UUID lessonId) {
    if (lessonRepo.closeIfOpen(lessonId) == 0) return;        // idempotent (OPEN->CLOSED)
    outboxRepo.save(OutboxMessage.of(lessonId, "LessonClosed",
                    writeJson(new LessonClosedEvent(lessonId, Instant.now()))));
    // lesson + outbox birlikte commit olur
}
```

---

## Case B — Asenkron rapor + e-posta (kuyruksuz, JobRunr)

**Problem:** Rapor üretimi **yüklü** → HTTP'yi bekletme. Ayrıca iş kuralı: kullanıcı 5 dk'da en
fazla 3 rapor. Broker yok (consumer aynı uygulamada) → JobRunr DB-kuyruğu yeter.

```
POST /reports → rate limit (sliding window) → Report(PENDING) → generate() ENQUEUE → 202
generate() [bg] → claim PENDING→PROCESSING → ağır hesap (tx dışı) → READY → sendEmail() ENQUEUE
sendEmail() [bg] → SMTP (retry) → emailedAt
GET /reports/{id} → durum sorgulama
```

**Kilit kararlar**
- **Rate limit serviste ve DB'den sayılır** (iş kuralı; in-memory değil, kalıcı + çok-instance).
  PENDING kayıtlar da sayılır → async pencerede baypas edilemez.
- **enqueue aynı transaction'da** → rapor commit olduysa "üret" işi kesin kuyrukta (outbox-style).
- **Ağır iş transaction DIŞINDA** → connection pool kilitlenmez.
- **Üretim ve e-posta ayrı job** → mail retry'ı ağır üretimi tekrarlamaz.
- **TOCTOU**: sert kota gerekiyorsa kullanıcıyı pessimistic lock ile sırala.

```java
@Transactional
public UUID requestReport(UUID userId, CreateReportRequest req) {
    if (reportRepo.countByUserIdAndCreatedAtAfter(userId, Instant.now().minus(WINDOW)) >= MAX)
        throw new RateLimitExceededException(MAX, WINDOW);     // 429
    Report r = reportRepo.save(Report.pending(userId, req.params()));
    jobScheduler.enqueue(() -> generationJob.generate(r.getId())); // aynı tx
    return r.getId();                                          // 202
}
```

---

## Case C — JobRunr'sız versiyon (@Scheduled + ShedLock / SKIP LOCKED)

**Problem:** Aynı işler JobRunr olmadan. JobRunr'sız kalınca **her `enqueue` → status'lü tablo +
poller**, **her `@Recurring` → `@Scheduled` + ShedLock** olur.

| JobRunr | JobRunr'sız karşılığı |
|---------|------------------------|
| `enqueue` (tek seferlik) | Status'lü tablo satırı (iş = satır) + poller |
| Worker havuzu | `@Scheduled` poller (+ lokal thread pool veya SKIP LOCKED) |
| `@Recurring` | `@Scheduled` + `@SchedulerLock` |
| Otomatik retry/back-off | Elle: `attempts` + `next_attempt_at` kolonları |
| Multi-instance tek-çalışma | ShedLock (singleton) **veya** SKIP LOCKED (dağıtım) |
| FAILED state / DLQ | `status = FAILED` |
| Dashboard | Yok — kendi log/metric/admin sorgun |

**Kritik ayrım**
- **ShedLock** = periyodik method'u **tek instance**'a kilitler (tekrarı önler) → outbox relay,
  "saati geleni tara" için doğru.
- **`FOR UPDATE SKIP LOCKED`** = işi **instance'lara böler** (paralellik) → iş kuyruğunu çok
  instance'ta drenaj etmek için doğru. JobRunr'ın içeride yaptığı budur.

```java
@Scheduled(fixedDelay = 5_000)
@SchedulerLock(name = "outbox-relay", lockAtMostFor = "PT1M")
@Transactional
public void publishPending() {
    for (OutboxMessage m : outboxRepo.findTop100ByPublishedFalseOrderByCreatedAt()) {
        rabbitTemplate.convertAndSend("lesson.exchange", "lesson.closed", m.getPayload());
        m.markPublished();
    }
}
```

> Kaybettiklerin: dashboard, otomatik retry/back-off, instant processing, kolay fan-out, zombie
> (stuck `PROCESSING`) temizliği. Bunlar yük olmaya başlayınca JobRunr'a dönmek mantıklı.

---

## Case D — OTP: aynı primitif, iki depolama (DB vs Redis)

**Problem:** "Bu kod, bu `(destination, type)` için şu an geçerli mi?" sorusunu cevaplayan, **yan
etkisiz**, tek-kullanımlık, süreli ve **çok-instance'ta tutarlı** bir OTP primitifi. Üstüne her akış
(hesap doğrulama, parola sıfırlama, 2FA) kompose olur.

**Kilit fikir:** depolama dışındaki her şey ortak. Kod üreteci (`SecureRandom`), kanal gönderimi
(`OtpSender` + dispatcher; SMS/EMAIL bean'leri otomatik bağlanır), `otp.*` config, enum'lar, hata
kataloğu ve DTO'lar **tek yerde**. Yalnızca **kodun nerede durduğu** değişiyor → iki birbirinin
yerine geçen varyant.

```
                 ┌─ ortak çekirdek: OtpCodeGenerator · OtpSenderDispatcher · OtpProperties
send/verify ─────┤                  · enums · OtpExceptionType · request/response DTO
                 └─ depolama:  ① DB (otps tablosu)        ② Redis (key + TTL)
```

| | ① DB (`/otp`) | ② Redis (`/otp/v2`) |
|---|---|---|
| Depolama | `otps` satırı | Redis hash, key = `otpv2:{type}:{channel}:{destination}` |
| Süre dolması | `expiry_time` kolonu, verify'da kontrol | **key TTL'i** — dolunca key yok olur, okunacak bir şey kalmaz |
| "Tek aktif" | `(destination, type)` için bulk `used=true` | `(type, channel, destination)` key'i yenisi **ezer** |
| Tek kullanım | `used = true` işaretle | doğru kod key'i **siler** (used alanına gerek yok) |
| Temizlik | `OtpCleanupJob` + ShedLock (expired/used satırları siler) | **yok** — TTL otomatik düşürür |
| Çok-instance | paylaşılan DB ile çalışır | paylaşılan Redis ile çalışır, DB round-trip yok |

**Redis varyantının mantığı (neden bu kadar sade):**
- **Süre = TTL.** Key'e validity süresi kadar TTL veriyoruz. Ayrı "expired mi?" kontrolü yok; süre
  dolunca `GET` boş döner ve bu **zaten** "geçersiz" demektir. Senin de istediğin nokta buydu.
- **`used` statüsü yok.** Doğru kodda key `DELETE` ediliyor (tek kullanım); yanlış/dolmuş kod TTL ile
  kendiliğinden gidiyor. "Kullanıldı" bayrağı tutmaya gerek kalmıyor.
- **Cleanup job yok.** TTL eviction temizliği üstleniyor — `OtpCleanupJob`/ShedLock gereksiz.
- **Tek aktif kendiliğinden.** Aynı key'i yeniden yazmak eskisini ezdiği için "yeni gönderim öncekini
  geçersiz kılar" kuralı bedavaya geliyor.
- **Multi-instance.** Tek doğruluk kaynağı Redis; DB transaction'ı yok, her yazma atomik tek komut.
- **Resend cooldown** da ayrı, kendi TTL'iyle ölen bir marker key (`otpv2:cd:...`).
- Key'de **kanal** olduğu için `/otp/v2/verify` `sendingChannel` de ister (kodun gönderildiği kanalla
  eşleşmeli); DB'li `/otp/verify` istemez.

**`attempts` (deneme sayacı) kararı — bilinçli olarak kaldırıldı.**
Eskiden hem DB'de "N yanlış denemede kilitle" mantığı vardı. Bunu **her iki varyanttan da** çıkardık:
- *Neden:* per-OTP sayaç, her yanlış denemede **state güncellemek** demek (DB satırı yaz / Redis alanı
  artır) — yani fazladan yazma ve karmaşa. Brute-force koruması için endpoint'lerde **zaten per-IP
  rate limit** var. Korumayı altyapı katmanına taşıdık.
- *Sonuç:* yanlış kod artık OTP'yi yakmıyor/sayaç tutmuyor — süre dolana ya da yeni kod gelene kadar
  tekrar denenebilir; brute-force'u rate limit + kısa TTL sınırlıyor. Hem entity hem Redis hash
  sadeleşti, `OTP_MAX_ATTEMPTS` hatası ve `max-attempts` config'i kalktı.

> **Karşılaştırma — ne zaman hangisi:** kalıcı kayıt/denetim (audit) istiyorsan DB; sıfır tablo
> büyümesi, cleanup'sız ve düşük maliyetli kendiliğinden-ölen OTP istiyorsan Redis. İkisi de aynı
> sözleşmeyi (send/verify) konuşuyor, akışlar koddan bağımsız.

---

# EK — Pattern Sözlüğü (hızlı referans)

Problem → çözen pattern eşlemesi. (İrtifa: sınıf seviyesinden sistemler-arası seviyeye.)

- **Outbox** — DB yazma + mesaj yayını atomikliği (dual-write). Mesajı aynı tx'te outbox
  tablosuna yaz, relay sonra publish eder.
- **Inbox / Idempotent consumer** — at-least-once teslimatta tekrar işlemeyi engelle. İşlenen
  mesaj id'sini kaydet; gelince "daha önce işledim mi?" bak.
- **Saga** — çok adımlı dağıtık işlem; bir adım patlarsa öncekileri **telafi** et (compensation).
  Orkestrasyon (merkezi) vs koreografi (event'lerle).
- **2PC / XA** — dağıtık atomik commit; güçlü tutarlılık ama kilit tutar, ölçekte pratik değil.
  Outbox/Saga pragmatik alternatif.
- **CQRS** — okuma/yazma modellerini ayır (aynı DB'den ayrı store'a kadar spektrum). Eventual
  consistency getirir.
- **Teslimat semantiği** — at-most/at-least/exactly-once. Pratikte: at-least-once + idempotency
  = "effectively once".
- **Resilience** — retry+back-off (geçici hatada), circuit breaker (closed/open/half-open),
  timeout, bulkhead, rate limiting. Spring'de **Resilience4j**.
- **Tetik türleri** — `enqueue` (olay → şimdi bir kez), `schedule(instant)` (gelecekte bir an,
  bir kez), `@Recurring`/`@Scheduled` (takvime göre tekrar).

> Sezgi: aşağı indikçe pattern "kodu nasıl düzenlerim", yukarı çıktıkça "sistemler nasıl
> güvenilir konuşur" sorusuna kayar.