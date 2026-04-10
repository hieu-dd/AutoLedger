# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

### Build
```bash
./gradlew :composeApp:assembleDebug   # Android APK
./gradlew :server:run                 # Start Ktor backend (port 8080)
```

### Code Style (ktlint)
```bash
./gradlew ktlintCheck    # Check formatting
./gradlew ktlintFormat   # Auto-fix formatting
```

### Tests
```bash
./gradlew :shared:commonTest          # Shared KMP tests
./gradlew :composeApp:testDebugUnitTest  # Android unit tests
```

## Architecture

**AutoLedger** is a local-first personal finance app using Kotlin Multiplatform (Android + iOS) with a Ktor backend.

### Module Structure

| Module | Purpose |
|---|---|
| `shared/` | KMP library — domain models, use cases, repository interfaces, Ktor API client, SQLDelight schema, sync engine, notification parser |
| `composeApp/` | Compose Multiplatform UI — screens, ViewModels, theme |
| `server/` | Ktor JVM backend — REST routes, Exposed ORM, PostgreSQL, JWT auth |
| `iosApp/` | iOS SwiftUI shell hosting ComposeUIViewController |

### Key Technologies

- **UI:** Compose Multiplatform 1.10.3, Material3
- **DI:** Koin 4.0.4
- **Local DB:** SQLDelight 2.0.2 (package: `org.bakarot.autoledger.shared.db`, driver: Android=OkHttp, iOS=Darwin)
- **HTTP Client:** Ktor Client 3.4.1
- **Server:** Ktor Server 3.4.1 + Exposed 0.61.0 + PostgreSQL
- **Settings/Tokens:** Multiplatform Settings 1.3.0
- **Serialization:** kotlinx-serialization 1.8.1
- **Auth:** JWT (15min access / 30-day refresh tokens), jBCrypt for passwords

### Data Flow

```
Composable → ViewModel (StateFlow) → UseCase → Repository Interface
                                                      ↓
                                         SQLDelight DAO + Ktor API client
```

### Core Architectural Rules

1. **Local-first:** All operations work offline; sync is always secondary.
2. **Server-authoritative conflict resolution:** Server `serverTimestamp` wins; client preserves tombstone semantics.
3. **Sync is idempotent:** Transaction IDs are natural idempotency keys.
4. **Retry policy:** No retry on 4xx (except 401 + token refresh). Transient errors: backoff 500ms → 1s → 2s with jitter.
5. **Token lifecycle:** Atomic persistence in Multiplatform Settings; single-flight refresh with client mutex.
6. **Error payloads:** Always include `code`, `message`, `requestId`, `details` fields.
7. **PII:** Never log PII; use redaction in observability code.

### Code Style

`.editorconfig` enforces: UTF-8, 4-space indentation, LF line endings. The `function-naming` and `wildcard-imports` ktlint rules are disabled.

### Technical Plans

Detailed technical specs live in `docs/technical-plan/`:
- `00-overview-architecture.md` — Module boundaries and NFRs
- `01-cross-cutting-error-timeout-retry.md` — Error taxonomy, retry policies, logging standards
- `02-data-model-and-schema.md` — Domain-to-SQLDelight-to-PostgreSQL mapping
- `03-sync-protocol-and-conflict.md` — Push/pull sync steps and conflict resolution
- `04-auth-and-token-lifecycle.md` — JWT flow and token rotation
- `05-notification-capture-and-parser.md` — Android notification listener and regex parser
- `backlog-ordered.md` — Prioritized feature backlog
- `test-strategy.md` — Testing approach

### Current Implementation Status

**Done:** Project structure, Gradle KMP setup, ktlint, Material3 theme, Login screen UI, Ktor server bootstrap.

**In progress:** Koin DI modules, SQLDelight schema, repository implementations, use cases.

**Not started:** Auth API integration, sync engine, notification listener, transaction CRUD screens, charts.
