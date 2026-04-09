# 02 - Data Model and Schema Plan

## Goal

Keep domain model, local SQLDelight schema, and server schema aligned for deterministic sync.

## Mapping rules

- Domain `Transaction.id` -> SQLDelight `TransactionEntity.id` -> PostgreSQL `transactions.id`.
- Domain datetime -> ISO-8601 string in local and server storage.
- Boolean flags (`isAutoCapture`, `isDeleted`) map to INTEGER(0/1) in SQLDelight and BOOLEAN on server.
- `syncStatus` only exists on client local DB.
- `serverTimestamp` exists on both sides for cursor sync.

## Migration strategy

1. Start schema versioning in SQLDelight and Exposed migration scripts.
2. Add forward-only migrations, never destructive in-app migration.
3. Add migration tests for each version jump.

## Data integrity checks

- Amount must be positive.
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
