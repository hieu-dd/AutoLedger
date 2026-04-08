# AutoLedger — Project Specification

## Overview

AutoLedger is a **Kotlin Multiplatform** personal finance management app that automatically captures transactions from banking/e-wallet notifications and provides visualization of spending habits. It follows a **local-first** architecture with cloud sync for backup and multi-device support.

**Platforms:** Android, iOS (via Compose Multiplatform) + Ktor Backend (JVM)

---

## 1. Tech Stack

| Component | Technology | Version | Purpose |
|---|---|---|---|
| Language | Kotlin | 2.3.20 | All modules |
| UI Framework | Compose Multiplatform | 1.10.3 | Cross-platform UI (Android + iOS) |
| Backend Framework | Ktor Server (Netty) | 3.4.1 | REST API |
| Local Database | SQLDelight | 2.0.2 | Cross-platform local DB (shared module) |
| Server Database | Exposed + PostgreSQL | 0.61.0 / 42.7.5 | Server-side ORM + DB |
| DI | Koin | 4.0.4 | All modules (KMP-friendly) |
| Navigation | Compose Navigation | 2.10.0 | Multiplatform navigation |
| HTTP Client | Ktor Client | 3.4.1 | Shared module — sync & auth |
| Serialization | kotlinx-serialization | 1.8.1 | JSON across all modules |
| Date/Time | kotlinx-datetime | 0.6.2 | Cross-platform date handling |
| Auth | Ktor Server Auth JWT | 3.4.1 | JWT authentication |
| Password Hashing | jBCrypt | 0.4 | Server-side |
| Connection Pool | HikariCP | 6.3.0 | Server-side |
| Charts | Koala Plot | 0.8.0 | KMP Compose chart library |
| Key-Value Storage | Multiplatform Settings | 1.3.0 | Tokens, preferences |
| Logging | Logback | 1.5.32 | Server-side |
| Design System | Material3 | 1.10.0-alpha05 | App UI components |

### Gradle Plugin Additions

| Plugin | Version | Purpose |
|---|---|---|
| `app.cash.sqldelight` | 2.0.2 | SQLDelight code generation |
| `org.jetbrains.kotlin.plugin.serialization` | 2.3.20 | kotlinx.serialization compiler |

---

## 2. Architecture

### 2.1 Module Structure

```
AutoLedger/
├── shared/              → Core business logic (KMP)
│   ├── commonMain/      → Domain models, repos, use cases, parsers, sync engine
│   ├── androidMain/     → AndroidSqliteDriver
│   ├── iosMain/         → NativeSqliteDriver
│   └── jvmMain/         → Platform.jvm (server doesn't use SQLDelight)
│
├── composeApp/          → UI layer (Compose Multiplatform)
│   ├── commonMain/      → Screens, navigation, ViewModels, theme
│   ├── androidMain/     → NotificationListenerService, MainActivity
│   └── iosMain/         → MainViewController
│
├── server/              → Ktor backend (JVM)
│   └── src/main/        → Routes, services, Exposed tables, JWT auth
│
└── iosApp/              → SwiftUI shell (hosts ComposeView)
```

### 2.2 Clean Architecture Layers

```
shared/commonMain/
  domain/
    model/          → Transaction, Category, Account, User, enums
    repository/     → Repository interfaces
    usecase/        → Use cases (pure business logic)
  data/
    local/          → SQLDelight data sources, DAOs
    remote/         → Ktor Client API service
    sync/           → Sync engine (push/pull orchestration)
    parser/         → Notification text parsing (regex-based)
  di/               → Koin modules

composeApp/commonMain/
  ui/screen/        → Composable screens
  navigation/       → NavHost, Route definitions, bottom nav
  viewmodel/        → ViewModels (StateFlow-based)
  theme/            → Material3 theme, colors, typography
  di/               → Koin UI modules

server/src/main/
  plugins/          → Ktor plugin configuration
  routes/           → Route definitions
  service/          → Business logic services
  db/tables/        → Exposed table definitions
  db/dao/           → Server-side DAOs
  auth/             → JWT config, password hashing
  model/request/    → Request DTOs
  model/response/   → Response DTOs
```

**Dependency Flow:** `UI → ViewModel → UseCase → Repository Interface ← Repository Impl → DataSource`

---

## 3. Features

### 3.1 Notification Capture (Android)

Listens to banking/e-wallet notifications via `NotificationListenerService`. Extracts transaction data (amount, merchant, type) using regex pattern matching.

**Supported sources (initial):**
- Banks: BCA, Mandiri, BNI, BRI
- E-wallets: GoPay, OVO, Dana, ShopeePay

**Pipeline:**
```
Notification → Filter (known package?) → Parse (regex) → Transaction → Save to DB → Show confirmation notification
```

**iOS:** Not possible natively. Manual entry only. Future: Share Extension for text input.

### 3.2 Transaction Management

- Add transaction: amount, type (income/expense), category, account source, date, notes
- Edit transaction: all fields editable
- Delete transaction: swipe-to-delete with undo, soft-delete for sync
- List with date range filter, search, and category filter
- Auto-captured transactions tagged with `isAutoCapture = true`

### 3.3 Data Visualization

- **Category pie/donut chart** — spending breakdown for selected period
- **Income vs Expense bar chart** — last 6 months comparison
- **Spending trend line chart** — last 12 months
- **Period selector** — week / month / 3 months / year / custom

### 3.4 Accounts & Wallets

- Manage accounts: bank accounts, e-wallets, cash, credit cards
- Track balance per account
- Multi-currency support (default: IDR)

### 3.5 Authentication

- Email + password registration and login
- JWT access token (15min) + refresh token (30 days)
- Stored securely via Multiplatform Settings

### 3.6 Sync

- **Local-first:** All operations work offline
- **Sync protocol:** Push unsynced → Pull server changes
- **Conflict resolution:** Last-write-wins (server is source of truth post-merge)
- **Triggers:** App launch (if >15min since last sync), manual pull-to-refresh, Settings button
- **Background:** Android WorkManager (every 6 hours), iOS BGTaskScheduler

---

## 4. Domain Models

```kotlin
data class Transaction(
    val id: String,                    // UUID, client-generated
    val amount: Double,
    val type: TransactionType,         // INCOME, EXPENSE
    val category: Category,
    val merchantOrDescription: String,
    val date: LocalDateTime,
    val accountSource: String,
    val notes: String?,
    val isAutoCapture: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val isDeleted: Boolean,            // soft delete for sync
    val syncStatus: SyncStatus         // PENDING, SYNCED, CONFLICT
)

enum class TransactionType { INCOME, EXPENSE }
enum class SyncStatus { PENDING, SYNCED, CONFLICT }

data class Category(
    val id: String,
    val name: String,
    val icon: String,                  // material icon name
    val color: Long,                   // ARGB
    val isDefault: Boolean
)

data class Account(
    val id: String,
    val name: String,
    val type: AccountType,             // BANK, EWALLET, CASH, CREDIT_CARD
    val balance: Double,
    val currency: String               // "IDR", "USD"
)

data class User(
    val id: String,
    val email: String,
    val displayName: String
)
```

**Default Categories:** Food, Transport, Entertainment, Shopping, Bills, Salary, Transfer, Other

---

## 5. Database Schema

### 5.1 Client-Side (SQLDelight)

```sql
CREATE TABLE TransactionEntity (
    id TEXT NOT NULL PRIMARY KEY,
    amount REAL NOT NULL,
    type TEXT NOT NULL,
    category_id TEXT NOT NULL,
    merchant_or_description TEXT NOT NULL,
    date TEXT NOT NULL,                -- ISO-8601
    account_source TEXT NOT NULL,
    notes TEXT,
    is_auto_capture INTEGER NOT NULL DEFAULT 0,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL,
    is_deleted INTEGER NOT NULL DEFAULT 0,
    sync_status TEXT NOT NULL DEFAULT 'PENDING',
    server_timestamp INTEGER
);

CREATE TABLE CategoryEntity (
    id TEXT NOT NULL PRIMARY KEY,
    name TEXT NOT NULL,
    icon TEXT NOT NULL,
    color INTEGER NOT NULL,
    is_default INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE AccountEntity (
    id TEXT NOT NULL PRIMARY KEY,
    name TEXT NOT NULL,
    type TEXT NOT NULL,
    balance REAL NOT NULL DEFAULT 0.0,
    currency TEXT NOT NULL DEFAULT 'IDR'
);

CREATE TABLE SyncMetaEntity (
    key TEXT NOT NULL PRIMARY KEY,
    value TEXT NOT NULL
);
```

### 5.2 Server-Side (Exposed / PostgreSQL)

```
users
  id VARCHAR(36) PK
  email VARCHAR(255) UNIQUE
  password_hash VARCHAR(255)
  display_name VARCHAR(255)
  created_at BIGINT

transactions
  id VARCHAR(36) PK
  user_id VARCHAR(36) FK → users.id
  amount DOUBLE
  type VARCHAR(20)
  category_id VARCHAR(36)
  merchant_or_description VARCHAR(500)
  date VARCHAR(30)
  account_source VARCHAR(100)
  notes VARCHAR(1000) NULLABLE
  is_auto_capture BOOLEAN
  created_at VARCHAR(30)
  updated_at VARCHAR(30)
  is_deleted BOOLEAN DEFAULT false
  server_timestamp BIGINT
  device_id VARCHAR(100)

INDEX idx_user_sync ON transactions(user_id, server_timestamp)
```

---

## 6. API Contract

**Base URL:** `http://{host}:8080/api/v1`

### Authentication

| Method | Endpoint | Body | Response |
|---|---|---|---|
| POST | `/auth/register` | `{ email, password, displayName }` | `{ accessToken, refreshToken, user }` |
| POST | `/auth/login` | `{ email, password }` | `{ accessToken, refreshToken, user }` |
| POST | `/auth/refresh` | `{ refreshToken }` | `{ accessToken, refreshToken }` |

### Sync

| Method | Endpoint | Body/Params | Response |
|---|---|---|---|
| POST | `/sync/push` | `{ deviceId, transactions: [...] }` | `{ accepted: [ids], conflicts: [...], serverTimestamp }` |
| GET | `/sync/pull?since={epochMillis}` | — | `{ transactions: [...], serverTimestamp }` |

### Transactions (read-only, for web/admin)

| Method | Endpoint | Params | Response |
|---|---|---|---|
| GET | `/transactions` | `?from=&to=&page=&size=` | `{ transactions, total, page }` |
| GET | `/transactions/{id}` | — | `{ transaction }` |

All authenticated endpoints require `Authorization: Bearer <accessToken>`.

---

## 7. Notification Parsing Design

### Pattern Registry

```kotlin
data class PatternRule(
    val packageNames: List<String>,
    val regex: Regex,
    val amountGroup: Int,
    val merchantGroup: Int?,
    val transactionType: TransactionType
)

// Example: BCA expense
PatternRule(
    packageNames = listOf("com.bca"),
    regex = Regex("""(?:Pengeluaran|Pembelian)\s+(?:sebesar\s+)?Rp\.?\s*([\d.,]+)\s+(?:di|ke)\s+(.+)"""),
    amountGroup = 1,
    merchantGroup = 2,
    transactionType = TransactionType.EXPENSE
)
```

### Android Service Registration

```xml
<service
    android:name=".service.TransactionNotificationService"
    android:label="AutoLedger Transaction Capture"
    android:permission="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE"
    android:exported="false">
    <intent-filter>
        <action android:name="android.service.notification.NotificationListenerService" />
    </intent-filter>
</service>
```

Permission granted via `Settings > Notification access`. Detected via `NotificationManagerCompat.getEnabledListenerPackages()`.

---

## 8. Sync Protocol

### Push Phase

1. Client collects transactions where `syncStatus = PENDING`
2. POST `/sync/push` with batch
3. Server per transaction:
   - **New ID:** INSERT, set `serverTimestamp = now()`, return in `accepted`
   - **Existing, client newer:** UPDATE, return in `accepted`
   - **Existing, server newer:** Return in `conflicts` (both versions)
4. Client marks accepted as `SYNCED`
5. Conflicts: server version wins, client overwrites local

### Pull Phase

1. Client reads `lastSyncTimestamp` from local storage
2. GET `/sync/pull?since={lastSyncTimestamp}`
3. Server returns all transactions where `serverTimestamp > since`
4. Client upserts into local DB, marks `SYNCED`
5. Updates `lastSyncTimestamp`

### Offline Behavior

All CRUD works offline. Unsynced items accumulate with `syncStatus = PENDING`. UI shows badge count of pending items. Sync resumes when connectivity returns.

---

## 9. App Screens

| Screen | Route | Description |
|---|---|---|
| Dashboard | `/dashboard` | Month summary, recent transactions, quick-add FAB |
| Transaction List | `/transactions` | Full list, filters, search, swipe-to-delete |
| Transaction Detail | `/transactions/{id}` | View/edit single transaction |
| Add Transaction | `/transactions/add` | Form: amount, type, category, account, date, notes |
| Charts | `/charts` | Pie chart, bar chart, trend line, period selector |
| Accounts | `/accounts` | Account list with balances |
| Settings | `/settings` | Notification toggle, sync, theme, logout |
| Login | `/auth/login` | Email + password |
| Register | `/auth/register` | Registration form |

**Bottom Nav:** Dashboard · Transactions · Charts · Settings

---

## 10. Code Sharing Strategy

| Code | Location | Shared? |
|---|---|---|
| Domain models | `shared/commonMain` | All platforms |
| Repository interfaces | `shared/commonMain` | All platforms |
| Use cases | `shared/commonMain` | All platforms |
| Notification parser | `shared/commonMain` | All platforms (testable) |
| SQLDelight schema | `shared/commonMain` | Android + iOS |
| SQLite drivers | `shared/androidMain`, `shared/iosMain` | Platform-specific |
| Ktor Client | `shared/commonMain` | Android + iOS |
| Ktor Client engine | `shared/androidMain` (OkHttp), `shared/iosMain` (Darwin) | Platform-specific |
| UI screens | `composeApp/commonMain` | Android + iOS |
| ViewModels | `composeApp/commonMain` | Android + iOS |
| NotificationListener | `composeApp/androidMain` | Android only |
| Server logic | `server/` | Server only |

---

## 11. Key Architectural Decisions

1. **SQLDelight over Room** — Room is Android-only. SQLDelight generates KMP code from SQL, sharing a single schema across Android and iOS.

2. **Koin over Hilt** — Hilt is Android-only and annotation-processor-based. Koin is pure Kotlin, works across all KMP targets including Ktor server.

3. **Server as Source of Truth** — During conflict resolution, the server version wins. Simpler than CRDT/vector clocks, acceptable for single-user multi-device scenario.

4. **Soft Deletes** — Transactions are never physically deleted on client. `isDeleted = true` syncs to server so other devices learn about deletions. Server can physically clean up after 90 days.

5. **Parser in shared/commonMain** — Even though only Android uses it via NotificationListenerService, the parsing logic is pure Kotlin string processing. Fully unit-testable without Android, reusable for iOS Share Extension later.

---

## 12. Implementation Phases

### Phase 1: Foundation (Priority: Critical)
> Database, DI, domain models, basic CRUD

- [ ] Add all dependencies to `libs.versions.toml` and module `build.gradle.kts`
- [ ] Configure SQLDelight plugin with platform drivers (Android, iOS)
- [ ] Define SQLDelight schema (all tables + queries)
- [ ] Implement domain models in `shared/commonMain/domain/model/`
- [ ] Implement repository interfaces in `shared/commonMain/domain/repository/`
- [ ] Implement `TransactionLocalDataSource` using SQLDelight
- [ ] Implement `TransactionRepositoryImpl` (local-only)
- [ ] Set up Koin modules in `shared` and `composeApp`
- [ ] Seed default categories

### Phase 2: Core UI (Priority: Critical)
> All screens with real data, fully functional offline app

- [ ] Set up Compose Navigation + bottom nav
- [ ] Define Material3 theme (colors, typography)
- [ ] Implement `AddTransactionScreen` + ViewModel
- [ ] Implement `TransactionListScreen` + ViewModel (date filter, search)
- [ ] Implement `TransactionDetailScreen` (view + edit)
- [ ] Implement `DashboardScreen` + ViewModel (summary + recent)
- [ ] Implement `AccountsScreen` + ViewModel
- [ ] Implement `SettingsScreen` (placeholder)

### Phase 3: Charts & Visualization (Priority: High)
> Data visualization screens

- [ ] Integrate Koala Plot library
- [ ] Implement `GetCategoryBreakdownUseCase`
- [ ] Implement `GetMonthlyTrendUseCase`
- [ ] Build `ChartsScreen`: pie chart, bar chart, trend line
- [ ] Add period selector (week/month/3mo/year/custom)

### Phase 4: Notification Capture (Priority: High)
> Android auto-capture from bank notifications

- [ ] Implement `NotificationParser` + `PatternRegistry` in shared
- [ ] Write unit tests for parser with sample notification texts
- [ ] Implement `TransactionNotificationService` in androidMain
- [ ] Register service in `AndroidManifest.xml`
- [ ] Add notification permission flow to Settings screen
- [ ] Show confirmation notification after auto-capture
- [ ] Add "captured transactions" filter in list

### Phase 5: Backend (Priority: High)
> Ktor server with auth and sync API

- [ ] Configure Ktor plugins (JSON, JWT, StatusPages)
- [ ] Set up Exposed + PostgreSQL + HikariCP
- [ ] Implement `PasswordHasher` (BCrypt), `JwtConfig`
- [ ] Implement `AuthService` + `AuthRoutes`
- [ ] Implement `SyncService` + `SyncRoutes`
- [ ] Add rate limiting and input validation
- [ ] Write integration tests

### Phase 6: Client Sync (Priority: Medium)
> Connect client to backend

- [ ] Implement `KtorApiService` with Ktor Client
- [ ] Configure auth interceptor + token refresh
- [ ] Implement `AuthRepositoryImpl` (Multiplatform Settings)
- [ ] Implement `SyncRepositoryImpl`
- [ ] Implement `SyncUseCase` (push-then-pull)
- [ ] Build Login/Register screens + AuthViewModel
- [ ] Add sync triggers (app launch, manual, background worker)
- [ ] Add sync status indicator in UI

### Phase 7: Polish (Priority: Medium)
> Production readiness

- [ ] Error handling for network, parse, sync failures
- [ ] Empty states for all screens
- [ ] Swipe-to-delete with undo snackbar
- [ ] Search in transaction list
- [ ] Export to CSV
- [ ] Onboarding flow (first launch)
- [ ] Dark mode (Material3 dynamic theming)
- [ ] Pagination for large transaction lists
- [ ] ProGuard/R8 rules for release

---

## 13. Critical Files to Modify

| File | Changes |
|---|---|
| `gradle/libs.versions.toml` | All new dependency declarations |
| `shared/build.gradle.kts` | SQLDelight plugin, serialization, Koin, Ktor Client, Settings |
| `composeApp/build.gradle.kts` | Koin-Compose, Navigation, Charts, Serialization |
| `server/build.gradle.kts` | Exposed, PostgreSQL, JWT, Koin-Ktor, BCrypt |
| `composeApp/src/androidMain/AndroidManifest.xml` | NotificationListenerService, INTERNET permission |
| `settings.gradle.kts` | No changes needed (modules already declared) |
