# 02 - Data Model and Schema Plan

## Goal

Keep domain model, local SQLDelight schema, and server schema aligned for deterministic sync.

## Mapping rules

- Domain `Transaction.id` -> SQLDelight `TransactionEntity.id` -> PostgreSQL `transactions.id`.
- Domain datetime -> ISO-8601 string in local and server storage.
- Boolean flags (`isAutoCapture`) map to INTEGER(0/1) in SQLDelight and BOOLEAN on server.
- `syncStatus` only exists on client local DB. *(Deferred to post-MVP)*
- `serverTimestamp` exists on both sides for cursor sync. *(Deferred to post-MVP; server schema retains it for future readiness)*

## MVP schema simplifications

- `amount` field: stored as `Long` (smallest currency unit, dong for VND) instead of `Double`. Maps to `INTEGER` in SQLDelight and `BIGINT` in PostgreSQL.
- `balance` field in AccountEntity: also `Long` / `INTEGER` for consistency.
- Default currency: `VND` (not `IDR`).
- MVP client schema omits: `is_deleted`, `sync_status`, `server_timestamp` columns from TransactionEntity, and the entire `SyncMetaEntity` table.
- These fields will be added via SQLDelight migration when sync is implemented.
- The domain model `Transaction` correspondingly omits `isDeleted` and `syncStatus` for MVP.

## Migration strategy

1. Start schema versioning in SQLDelight and Exposed migration scripts.
2. Add forward-only migrations, never destructive in-app migration.
3. Add migration tests for each version jump.

## Data integrity checks

- Amount must be positive (`Long > 0`, representing smallest currency unit).
- Type must be one of `INCOME`, `EXPENSE`.
- Category and account references must exist locally.
- `updatedAt >= createdAt`.

## Failure cases and handling

- Enum mismatch from server payload: map to `DataError` and skip bad row, continue batch.
- Corrupted local row: quarantine row ID in telemetry and continue reading others.
- Migration failure: block startup with recoverable message + export diagnostics.

## Acceptance criteria

- Shared model serialization/deserialization test passes on Android and iOS.
- Round-trip tests pass: Domain -> DTO -> Domain and Domain -> Entity -> Domain.
- Amount round-trip: verify Long value survives Domain -> DTO -> Domain and Domain -> Entity -> Domain without precision loss.
