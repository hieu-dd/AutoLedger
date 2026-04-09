# 00 - Overview Architecture

## Goal

Deliver a production-ready local-first finance app with reliable auth, sync, and Android notification capture.

## Implementation order

1. Foundation in `shared`: domain model, SQLDelight schema, repository interfaces, use cases.
2. Server in `server`: auth, sync, validation, error handling.
3. Client data layer: Ktor API service, token refresh, sync orchestrator.
4. UI routes in `composeApp`: navigation + per-screen ViewModel.
5. Android notification service + parser and tests.
6. Reliability hardening: timeout, retries, metrics, and integration tests.

## Module boundaries

- `shared/commonMain`: domain, use cases, parser, sync orchestration, repository contracts.
- `shared/androidMain` and `shared/iosMain`: platform drivers and HTTP engine specifics.
- `composeApp/commonMain`: routes, ViewModels, UI state machines.
- `composeApp/androidMain`: notification listener service.
- `server/src/main`: Ktor routes, service, Exposed DAO/table, JWT security.

## Key non-functional requirements

- App remains usable offline for all transaction/account CRUD.
- Every network path has timeout and controlled retry behavior.
- Every endpoint returns normalized error payload.
- Every sync action is idempotent or safely retryable.
- Logs avoid PII leakage and include correlation identifiers.

## Shared conventions

- IDs: UUID v4 generated on client.
- Time: UTC at transport, localized at UI formatting.
- Conflict policy: server wins, but conflict event is counted and surfaced.
- Delete policy: soft delete with `isDeleted=true`.
- Sync states: `PENDING`, `SYNCED`, `CONFLICT`.
