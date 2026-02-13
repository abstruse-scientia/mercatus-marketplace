// ...existing code...

## License

This project is proprietary software. All rights reserved.

---

## Technical Deep Dive

### 1️⃣ Authentication & Security

#### Login (JWT)
- **Token Type:** JWT (JSON Web Token)
- **Expiry:** 30 minutes (`30 * 60 * 1000` ms)
- **Claims:** `username`, `email`, `roles`, `issuedAt`, `expiration`
- **Issuer:** "Mercatus"
- **Signing:** HMAC with secret key via `JwtKeyProvider`
- **Roles:** Stored as comma-separated string (e.g., `"ROLE_USER,ROLE_ADMIN"`)

#### Refresh Token
- **Storage:** Database (`refresh_token` table)
- **Format:** 64-byte random token, Base64 URL-encoded
- **Hashing:** SHA-256 (only hash stored in DB, never raw token)
- **Expiry:** 7 days
- **Rotation:** ✅ Yes - Old token revoked, new token issued on refresh
- **Invalidation:** `isRevoked` flag in database

#### Logout
- **Status:** ❌ **NOT IMPLEMENTED**
- No logout endpoint exists. Frontend should discard tokens client-side.
- Refresh tokens can be revoked via `revokeRefreshToken()` but no endpoint exposes this.

#### Password Hashing
- **Algorithm:** BCrypt (Spring Security default)
- **Strength:** Default (10 rounds)
- **Compromised Password Check:** ✅ Yes - `HaveIBeenPwnedRestApiPasswordChecker` enabled

#### Security Config (Public Paths)
```
permitAll paths:
- api/v1/products/**
- api/v1/contacts/**
- api/v1/auth/**
- /error
- api/v1/csrf-token
```
⚠️ **Note:** Current config has `anyRequest().permitAll()` - all endpoints are public! This appears to be a development configuration that needs tightening for production.

---

### 2️⃣ Product & Catalog

#### Product States
- **ACTIVE/INACTIVE:** ✅ Yes - `isActive` boolean field (default: `true`)
- Active filtering via `findAllByIsActiveTrue()` and `filter(Product::getIsActive)`

#### Price Handling
- **Strategy:** **SNAPSHOT** at order time
- `OrderItem.priceSnapshot` captures price when order is placed
- Cart displays live prices from `Product.price`

#### Pagination & Sorting
- ✅ **Yes** - Full Spring Data `Pageable` support
- Products: `GET /api/v1/products?page=0&size=10&sort=name,asc`
- Orders: Forced sort by `createdAt DESC`, max page size: 10

#### Soft Delete
- ✅ **Yes** - Products use `isActive` flag (soft delete via `deactivateProduct()`)
- No hard delete endpoint exposed

---

### 3️⃣ Cart

#### Cart Ownership
- **Dual support:** Both user-based AND anonymous (session-based)
- Guest: Cart linked to `sessionId`
- Authenticated: Cart linked to `userId`
- **Cart Merge:** ✅ When guest logs in, guest cart merges into user cart
  - Same product: quantities are summed
  - Different products: moved to user cart
  - Guest cart deleted after merge

#### Price Calculation
- **When:** On-demand when `getCartDetails()` is called
- **How:** Live lookup of `Product.price` × quantity per item
- Subtotal calculated by iterating all cart items

#### Concurrency Handling
- **Pessimistic Locking:** ✅ Yes - `PESSIMISTIC_WRITE` lock for checkout
- `findActiveCartForUpdate()` uses `SELECT ... FOR UPDATE`
- Prevents double-checkout of same cart with different order references

#### Max Quantity Rules
- ❌ **No** - No maximum quantity limit per item
- Only validation: quantity must be > 0

---

### 4️⃣ Orders

#### Order State Machine
```
CREATED ──────────────┬──→ PAYMENT_PENDING ──→ CONFIRMED ──→ SHIPPED ──→ DELIVERED
                      │                              │
                      └──→ CANCELLED ←───────────────┘
                                    ↑
                              (payment failed)
                                    │
                              ──→ RETURNED (from DELIVERED)
```

| State | Description |
|-------|-------------|
| `CREATED` | Order placed, awaiting payment initiation |
| `PAYMENT_PENDING` | Payment intent created, awaiting confirmation |
| `CONFIRMED` | Payment successful, inventory confirmed |
| `CANCELLED` | Order cancelled or payment failed |
| `SHIPPED` | Order shipped (manual update) |
| `DELIVERED` | Order delivered (manual update) |
| `RETURNED` | Order returned (manual update) |

#### Cancellation Rules
- ✅ **Allowed only in `CREATED` state**
- ❌ Cannot cancel after `PAYMENT_PENDING` or later
- Releases inventory reservations on cancel
- Sets `OrderPaymentStatus` to `CANCELLED`

#### Inventory Reservation
- **Type:** **SOFT RESERVATION** with expiry
- Reservation created at order placement (10-minute expiry)
- `StockReservation` entity tracks: `reservationKey`, `sku`, `quantity`, `expiresAt`, `status`
- States: `RESERVED` → `CONFIRMED` (payment success) or `RELEASED` (cancelled/failed)
- Reserved stock tracked separately: `availableStock = totalStock - reservedStock`

#### Order Immutability
- ✅ **Yes** - After placement, order items cannot be modified
- Only status transitions allowed
- Price snapshot preserved in `OrderItem.priceSnapshot`

---

### 5️⃣ Payments (Stripe Integration)

#### Payment Model

**Entities:**
- `Payment` - Internal payment record
- `ProcessedPaymentEvent` - Webhook deduplication

**Payment States:**
| State | Description |
|-------|-------------|
| `CREATED` | Payment record created internally |
| `PROCESSING` | Stripe PaymentIntent created, awaiting completion |
| `SUCCESS` | Payment confirmed via webhook |
| `FAILED` | Payment failed via webhook |

#### Stripe Flow

1. **PaymentIntent Creation:**
   - **When:** `POST /api/v1/payment` called by frontend
   - **Or:** `initiatePayment(orderId, userId)` from OrderService
   - Creates internal `Payment` record first
   - Then creates Stripe PaymentIntent
   - Returns `clientSecret` for frontend Stripe.js

2. **Webhook Verification:**
   - ✅ **Yes** - Full signature verification
   - Uses `Webhook.constructEvent(payload, signature, webhookSecret)`
   - Raw payload passed directly (no JSON parsing before verification)
   - Invalid signature throws `IllegalArgumentException`

3. **Deduplication:**
   - ✅ **Persisted event IDs** in `ProcessedPaymentEvent` table
   - Before processing: `if (eventRepository.existsById(event.getId())) return;`
   - After processing: Event ID saved to database
   - Prevents duplicate processing on Stripe retries

4. **Idempotency:**
   - **Payment creation:** Checks `findByOrderReference()` - throws if exists
   - **Order placement:** Returns existing order if `orderReference` already exists
   - **Success marking:** Returns early if already `SUCCESS`
   - **Stock reservation:** Returns existing reservation if `reservationKey` exists

---

### 6️⃣ Webhooks (Critical Details)

#### Signature Verification
- **Raw body:** ✅ Yes - Raw `String payload` passed to controller
- **Library:** Stripe Java SDK `Webhook.constructEvent()`
- **Secret:** `${stripe.webhook.secret}` from config

#### Retry Handling
- **Strategy:** Idempotent processing + event storage
- If Stripe retries (up to 5 times over days):
  1. Event ID checked against `ProcessedPaymentEvent` table
  2. If already processed → return 200 OK immediately
  3. If new → process and store event ID
- **No failure tracking** - relies on Stripe's retry mechanism

#### Event Storage
- ✅ **Persisted** - `ProcessedPaymentEvent` entity
- Fields: `eventId` (PK), `processedAt` (auto-timestamp)
- Survives server restarts
- Used for deduplication

#### Handled Events
```java
- payment_intent.succeeded  → handlePaymentSuccess()
- payment_intent.payment_failed → handlePaymentFailure()
- (other events ignored)
```

---

### 7️⃣ Testing

#### Unit Tests
- **Count:** ~14 tests (in `CartServiceImplTest.java`)
- **Coverage:** Cart operations (add, remove, update quantity)
- **Framework:** JUnit 5 + Mockito

#### Integration Tests
- **Count:** ~10 tests across multiple files
- **Flows covered:**
  - Inventory: reserve, release, confirm, add stock, get available
  - Order: place order (concurrent), cancel order, get orders
  - Address: delete address

#### Testcontainers
- ✅ **MySQL 8.0** container for integration tests
- **Config:** `jdbc:tc:mysql:8.0://localhost/mercatus_test`
- **Covers:**
  - Full database integration (real MySQL)
  - Flyway migrations
  - Transaction rollback
  - Optimistic locking scenarios
  - Concurrent order placement

#### Test Profiles
- `@ActiveProfiles("test")` - Uses `application-test.properties`
- Security beans excluded with `@Profile("!test")`

---

### 8️⃣ Infrastructure / Ops

#### Profiles
| Profile | Purpose | Database |
|---------|---------|----------|
| `dev` | Local development | `localhost:3306/mercatus` |
| `prod` | Production | Environment variables |
| `test` | Integration tests | Testcontainers MySQL |

#### Docker
- ❌ **No Dockerfile**
- ❌ **No docker-compose**
- Application must be run via Maven or JAR

#### Actuator
- ❌ **Not configured**
- No health checks, metrics, or management endpoints
- `spring-boot-starter-actuator` not in dependencies

#### Logging
- **Format (Console):** Colored pattern with timestamp, level, thread, logger
  ```
  %green(%d{HH:mm:ss.SSS}) %blue(%-5level) %red([%thread]) %yellow(%logger{15}) - %msg%n
  ```
- **Format (File):** JSON structured logging
  ```json
  {"timestamp":"...","level":"...","logger":"...","message":"..."}
  ```
- **Trace ID:** ❌ No distributed tracing (no Sleuth/Micrometer)
- **Log File:** `D:/LogFiles/mercatus.log` (configurable via `LOG_FILE_NAME`)
- **Debug enabled:** `org.springframework.security=DEBUG`

---

## Security Recommendations for Production

1. **Fix Security Config:** Change `anyRequest().permitAll()` to proper authorization rules
2. **Add Logout Endpoint:** Implement token revocation endpoint
3. **Add Actuator:** Enable health checks for load balancers
4. **Add Distributed Tracing:** Consider Spring Cloud Sleuth + Zipkin
5. **Dockerize:** Add Dockerfile and docker-compose for consistent deployments
6. **Rate Limiting:** Add rate limiting for auth endpoints
7. **HTTPS:** Ensure TLS termination in production

