# Ordered Backlog Tasks

This backlog is ordered from foundation work to production hardening.

## Conventions

- Priority: P0 (must-have), P1 (important), P2 (polish).
- Board states: TODO -> IN_PROGRESS -> BLOCKED -> REVIEW -> DONE.
- Task ID format: `AL-xxx`.
- **Target market:** Vietnam. Priority bank: Vietcombank (VCB).
- **MVP = Phase 1 + Phase 2 + Phase 3.** Notification capture is core value proposition.

## Phase 1 - Foundation (P0)

### AL-001 - Finalize dependencies and Gradle plugins
- Priority: P0
- Status: DONE
- Modules: `gradle/libs.versions.toml`, `shared/build.gradle.kts`, `composeApp/build.gradle.kts`, `server/build.gradle.kts`
- Steps: add versions from `SPECs.md`, enable SQLDelight and serialization plugins.
- Failure cases: dependency conflicts, plugin mismatch, unresolved transitive dependencies.
- Done when: `./gradlew help` and Gradle sync both pass.
- Depends on: none

### AL-002 - Implement domain models and enums
- Priority: P0
- Modules: `shared/src/commonMain/.../domain/model`
- Steps: add `Transaction` (amount as Long, no sync fields), `Category`, `Account`, `User`, `TransactionType`, `AccountType`, and date serialization rules. `SyncStatus` enum deferred to post-MVP.
- Failure cases: enum mismatch between local DB and API payloads.
- Done when: serialization/deserialization unit tests pass.
- Depends on: AL-001

### AL-003 - Define repository interfaces and use-case contracts
- Priority: P0
- Modules: `shared/src/commonMain/.../domain/repository`, `shared/src/commonMain/.../domain/usecase`
- Steps: define contracts for transactions, accounts, and categories with typed error results. Auth and sync repository interfaces deferred to post-MVP.
- Failure cases: unclear contracts that cannot represent timeout/data errors.
- Done when: data layer and ViewModel integration points are stable.
- Depends on: AL-002

### AL-004 - Create SQLDelight schema and queries
- Priority: P0
- Modules: `shared/src/commonMain/sqldelight/**`
- Steps: create tables (`TransactionEntity`, `CategoryEntity`, `AccountEntity`) and CRUD/filter/search queries. MVP schema omits `is_deleted`, `sync_status`, `server_timestamp` columns and `SyncMetaEntity` table. Amount as `INTEGER` (Long). Default currency `VND`.
- Failure cases: migration failures, date/boolean type mismatches.
- Done when: SQLDelight code generation passes.
- Depends on: AL-001, AL-002

### AL-005 - Implement local data sources and repository implementations
- Priority: P0
- Modules: `shared/src/commonMain/.../data/local`, `shared/src/commonMain/.../data/repository`
- Steps: implement DAO wrappers, local-first CRUD, and hard delete (MVP — no soft-delete, no sync status updates).
- Failure cases: write failures, concurrent updates, stale reads.
- Done when: CRUD + delete + undo unit tests pass.
- Depends on: AL-003, AL-004

### AL-006 - Configure Koin modules
- Priority: P0
- Modules: `shared/src/commonMain/.../di`, `composeApp/src/commonMain/.../di`
- Steps: register data sources, repositories, use cases, and ViewModels.
- Failure cases: missing bindings, circular dependencies.
- Done when: app boots with a valid Koin graph.
- Depends on: AL-005

### AL-007 - Seed default categories
- Priority: P0
- Modules: local bootstrap/init path
- Steps: seed Food, Transport, Entertainment, Shopping, Bills, Salary, Transfer, Other.
- Failure cases: duplicate inserts, startup race condition.
- Done when: fresh install contains all default categories.
- Depends on: AL-005

## Phase 2 - Notification Capture (P0 — MVP Critical)

### AL-019 - Implement PatternRegistry and NotificationParser
- Priority: P0
- Modules: `shared/src/commonMain/.../parser`
- Steps: add rules for Vietcombank (VCB, priority), then BIDV, Techcombank, MBBank, ACB, TPBank, MoMo, ZaloPay, VNPay, ShopeePay. Normalize Vietnamese amount parsing (strip `.`/`,` separators, handle `VND`/`d`/`đ` suffixes, convert to Long).
- Failure cases: false positives, decimal separator parsing issues, incorrect sign detection.
- Done when: parser unit test coverage reaches agreed threshold with VCB samples.
- Depends on: AL-002

### AL-020 - Implement Android TransactionNotificationService
- Priority: P0
- Modules: `composeApp/src/androidMain/.../service`, `composeApp/src/androidMain/AndroidManifest.xml`
- Steps: register listener service, filter packages, parse payload, save local transaction with `isAutoCapture=true`, show confirmation notification.
- Failure cases: revoked permission, OEM service kills, duplicate captures.
- Done when: sample Vietcombank notifications create transactions correctly.
- Depends on: AL-019, AL-005

### AL-021 - Implement notification permission flow in Settings
- Priority: P0
- Route: `/settings`
- Steps: detect listener enabled state and deep-link to system settings when disabled.
- Failure cases: vendor-specific ROM behavior differences.
- Done when: permission status is accurate and actionable.
- Depends on: AL-020, AL-015

## Phase 3 - Core UI and Navigation (P0 — MVP)

### AL-008 - Build navigation graph and route constants
- Priority: P0
- Modules: `composeApp/src/commonMain/.../navigation`
- Steps: implement all routes from spec and bottom nav (Dashboard, Transactions, Charts, Settings).
- Failure cases: invalid argument parsing for `/transactions/{id}`.
- Done when: stable navigation and back stack behavior.
- Depends on: AL-006

### AL-009 - Implement Material3 theme and app scaffold
- Priority: P1
- Modules: `composeApp/src/commonMain/.../theme`, `composeApp/src/commonMain/.../App.kt`
- Steps: create color/typography/shape tokens and common scaffold.
- Failure cases: poor contrast and broken small-screen layouts.
- Done when: consistent baseline UI on Android and iOS.
- Depends on: AL-008

### AL-010 - Implement Add Transaction screen + ViewModel
- Priority: P0
- Route: `/transactions/add`
- Steps: form state, validation (amount > 0, Long input), local save, duplicate-submit guard.
- Failure cases: validation bugs, DB save failure.
- Done when: offline transaction creation is reliable.
- Depends on: AL-005, AL-008

### AL-011 - Implement Transaction List screen + ViewModel
- Priority: P0
- Route: `/transactions`
- Steps: list rendering, date/category filters, search, swipe-to-delete with undo (hard delete for MVP), "auto-captured" filter.
- Failure cases: lag on large queries, incorrect undo rollback.
- Done when: filters/search/undo behave correctly.
- Depends on: AL-010

### AL-012 - Implement Transaction Detail screen + ViewModel
- Priority: P0
- Route: `/transactions/{id}`
- Steps: load by id, edit all fields, save locally.
- Failure cases: not-found ID, stale object updates.
- Done when: edit flow persists and list reflects updates.
- Depends on: AL-011

### AL-013 - Implement Dashboard screen + ViewModel
- Priority: P0
- Route: `/dashboard`
- Steps: monthly summary, recent transactions, quick-add FAB, local-first rendering.
- Failure cases: overlapping empty/loading/error states.
- Done when: dashboard is usable offline and refreshes safely.
- Depends on: AL-011

### AL-014 - Implement Accounts screen + ViewModel
- Priority: P1
- Route: `/accounts`
- Steps: account list, balances (Long amounts), CRUD with validation.
- Failure cases: deleting accounts that still have linked transactions.
- Done when: account flow works with balance updates.
- Depends on: AL-005, AL-008

### AL-015 - Implement Settings baseline screen
- Priority: P1
- Route: `/settings`
- Steps: notification permission toggle, theme settings placeholder, sync placeholder.
- Failure cases: session state not reflected correctly.
- Done when: settings screen shows notification status and is ready for sync/auth extension.
- Depends on: AL-008

## Phase 4 - Charts and Analytics (P1 — Post-MVP)

### AL-016 - Integrate Koala Plot
- Priority: P1
- Modules: `composeApp/build.gradle.kts`, chart UI components
- Steps: add dependency and shared chart primitives.
- Failure cases: rendering issues on iOS target.
- Done when: Android and iOS builds pass with chart module.
- Depends on: AL-009

### AL-017 - Implement aggregation use cases
- Priority: P1
- Modules: `shared/src/commonMain/.../domain/usecase`
- Steps: implement `GetCategoryBreakdownUseCase`, `GetMonthlyTrendUseCase`, `GetIncomeVsExpenseUseCase`.
- Failure cases: incorrect period slicing and timezone boundary bugs.
- Done when: aggregation unit tests pass with fixtures.
- Depends on: AL-005

### AL-018 - Implement Charts screen and period selector
- Priority: P1
- Route: `/charts`
- Steps: render pie/bar/line with week/month/3 months/year/custom periods.
- Failure cases: crashes on empty datasets, UI lag on large data.
- Done when: charts render stably with proper empty states.
- Depends on: AL-016, AL-017

## Phase 5 - Backend Auth and Sync (P1 — Post-MVP)

### AL-022 - Configure Ktor plugins, StatusPages, and validation
- Priority: P1
- Modules: `server/src/main/.../plugins`
- Steps: install ContentNegotiation, Authentication, StatusPages, and standard error mapping.
- Failure cases: uncaught exceptions leaking stack traces.
- Done when: all endpoints return normalized error payloads.
- Depends on: AL-001

### AL-023 - Configure Exposed + PostgreSQL + HikariCP
- Priority: P1
- Modules: `server/src/main/.../db`
- Steps: configure pool/timeouts/migrations and create `users` + `transactions` tables with indexes. Amount as BIGINT.
- Failure cases: pool exhaustion, transaction deadlocks.
- Done when: server starts and DB smoke tests pass.
- Depends on: AL-022

### AL-024 - Implement PasswordHasher and JwtConfig
- Priority: P1
- Modules: `server/src/main/.../auth`
- Steps: BCrypt hash/verify and JWT issue/verify for access + refresh tokens.
- Failure cases: clock skew, invalid signing config, token parsing errors.
- Done when: auth primitive unit tests pass.
- Depends on: AL-022

### AL-025 - Implement AuthService and AuthRoutes
- Priority: P1
- APIs: `/auth/register`, `/auth/login`, `/auth/refresh`
- Steps: add service logic, DTO mappings, route handlers, rate limiting, and input validation.
- Failure cases: duplicate-email race, brute-force login attempts.
- Done when: integration tests pass for all auth endpoints.
- Depends on: AL-023, AL-024

### AL-026 - Implement SyncService and SyncRoutes
- Priority: P1
- APIs: `/sync/push`, `/sync/pull`
- Steps: implement push batch acceptance (MVP: server always accepts, no conflict resolution) and pull endpoint for full data dump (MVP: used only for empty-client restore).
- Failure cases: oversized payloads, slow queries.
- Done when: sync integration tests pass.
- Depends on: AL-023, AL-024

### AL-027 - Implement read-only transactions routes
- Priority: P1
- APIs: `/transactions`, `/transactions/{id}`
- Steps: add pagination, filters, and ownership checks.
- Failure cases: cross-user data leakage.
- Done when: API tests pass with auth and ownership rules.
- Depends on: AL-023, AL-024

## Phase 6 - Client Remote and Sync Engine (P1 — Post-MVP)

### AL-028 - Implement KtorApiService client
- Priority: P1
- Modules: `shared/src/commonMain/.../data/remote`
- Steps: implement auth/sync API calls, DTO serialization, and timeout config.
- Failure cases: JSON schema mismatch, incorrect timeout/error mapping.
- Done when: remote layer unit tests pass.
- Depends on: AL-025, AL-026

### AL-029 - Implement auth interceptor and single-flight refresh
- Priority: P1
- Modules: HTTP client pipeline
- Steps: inject bearer token, refresh once on 401, retry original request once, protect refresh with mutex.
- Failure cases: infinite refresh loop, token overwrite race.
- Done when: concurrent request tests pass.
- Depends on: AL-028

### AL-030 - Implement AuthRepository and secure token storage
- Priority: P1
- Modules: `shared/src/commonMain/.../data/repository/auth`
- Steps: persist/read/clear tokens and session metadata via Multiplatform Settings.
- Failure cases: partial writes, stale session state.
- Done when: login/logout lifecycle tests pass.
- Depends on: AL-029

### AL-031 - Implement SyncRepository and SyncUseCase
- Priority: P1
- Modules: `shared/src/commonMain/.../data/sync`, `shared/src/commonMain/.../domain/usecase`
- Steps: MVP sync = one-way push (post all local transactions to server) + pull only when local DB is empty. Add SQLDelight migration for sync fields (is_deleted, sync_status, server_timestamp, SyncMetaEntity) when implementing full sync. Orchestrate push-then-pull for post-MVP, mark accepted/conflict states, update `lastSyncTimestamp`.
- Failure cases: incorrect local state after partial push response.
- Done when: client-server sync integration test passes.
- Depends on: AL-028, AL-005

### AL-032 - Implement Login/Register screens + AuthViewModel
- Priority: P1
- Routes: `/auth/login`, `/auth/register`
- Steps: complete form UX, error mapping, and auth-state-based routing.
- Failure cases: wrong error mapping, lost form state after recreation.
- Done when: auth UI e2e tests pass.
- Depends on: AL-030, AL-008

### AL-033 - Implement sync triggers and status indicators
- Priority: P1
- Modules: app startup, settings, list/dashboard pull-to-refresh
- Steps: MVP: manual push from Settings only. Post-MVP: trigger sync on app launch (>15m), pull-to-refresh, background hooks; show pending badge and last sync.
- Failure cases: multiple concurrent sync sessions.
- Done when: only one sync session can run at a time.
- Depends on: AL-031, AL-013, AL-015

## Phase 7 - Hardening and Release (P1/P2 — Post-MVP)

### AL-034 - Complete error-handling pass
- Priority: P1
- Scope: all modules
- Steps: finalize typed error-to-UI mapping and apply timeout/retry policies everywhere.
- Failure cases: unhandled payload shape causing crashes.
- Done when: cross-cutting error matrix is fully implemented.
- Depends on: AL-033

### AL-035 - Add empty/error/loading states + accessibility
- Priority: P1
- Scope: all screens
- Steps: unify state components and improve accessibility labels/typography/readability.
- Failure cases: missing fallback state on key screens.
- Done when: all routes include complete state coverage.
- Depends on: AL-018, AL-032

### AL-036 - Optimize transaction list pagination
- Priority: P1
- Scope: `/transactions`
- Steps: implement incremental paging, query tuning, memory guards.
- Failure cases: duplicate rows, scroll position jumps.
- Done when: large datasets are smooth and stable.
- Depends on: AL-011

### AL-037 - Implement CSV export
- Priority: P2
- Scope: shared export + platform file sharing
- Steps: implement exporter, permissions, and share/save flow. Amount formatting: Long -> human-readable with currency.
- Failure cases: encoding issues, incorrect currency/date formats.
- Done when: exported CSV opens correctly with valid columns.
- Depends on: AL-011

### AL-038 - Implement onboarding and first-launch guidance
- Priority: P2
- Scope: compose app
- Steps: add first-run flow, notification permission guidance, and Vietnamese bank setup hints.
- Failure cases: onboarding loop or missing completion state persistence.
- Done when: new users can finish setup in one guided flow.
- Depends on: AL-021, AL-032

### AL-039 - Add dark mode and dynamic theming
- Priority: P2
- Scope: theme + settings
- Steps: add dark mode toggle and persist preference.
- Failure cases: poor contrast and unreadable chart palettes.
- Done when: theme switch works smoothly on Android and iOS.
- Depends on: AL-009, AL-015

### AL-040 - Release hardening
- Priority: P1
- Scope: composeApp + server
- Steps: finalize ProGuard/R8 rules, release checks, redaction audit, integration and smoke tests.
- Failure cases: minification breaks serialization/reflection paths.
- Done when: release candidate build passes all smoke tests.
- Depends on: AL-034, AL-035, AL-036

### AL-041 - Add more Vietnamese bank parsers
- Priority: P1
- Scope: `shared/src/commonMain/.../parser`
- Steps: add parser rules for BIDV, Techcombank, MBBank, ACB, TPBank, MoMo, ZaloPay, VNPay, ShopeePay with unit tests for each.
- Failure cases: format differences between banks, false positives across providers.
- Done when: each new provider has passing unit tests with real sample texts.
- Depends on: AL-019

## Suggested execution lanes

- Lane A (Shared/Data): AL-001 -> AL-007 -> AL-019 -> AL-017 -> AL-028 -> AL-031
- Lane B (UI): AL-008 -> AL-015 -> AL-018 -> AL-032 -> AL-033
- Lane C (Android capture): AL-020 -> AL-021 (immediately after AL-019)
- Lane D (Server): AL-022 -> AL-027

## Milestone checkpoints

- **M0 (Notification MVP):** AL-001..AL-007 + AL-019..AL-021 complete. Can capture Vietcombank notifications and store them locally.
- **M1 (Offline MVP):** + AL-008..AL-015 complete. Full offline app with CRUD, notification capture, and all screens.
- **M2 (Sync Beta):** + AL-022..AL-033 complete. Server running, one-way push sync working.
- **M3 (Production):** + AL-034..AL-041 complete. Polished, multi-bank, release-ready.
