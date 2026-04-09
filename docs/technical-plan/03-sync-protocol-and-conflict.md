# 03 - Sync Protocol and Conflict Handling

## Goal

Implement reliable push-then-pull sync with server-authoritative conflict resolution.

## Push phase steps

1. Query local rows with `syncStatus = PENDING`.
2. Build batch with `deviceId` and transaction list.
3. POST `/sync/push`.
4. Mark accepted IDs as `SYNCED`.
5. For conflict IDs, overwrite local row with server version and mark `SYNCED`.

## Pull phase steps

1. Read `lastSyncTimestamp` from `SyncMetaEntity`.
2. GET `/sync/pull?since=...`.
3. Upsert rows from server into local DB.
4. Update `lastSyncTimestamp` to server response timestamp.

## Conflict model

- Rule: if server `serverTimestamp` is newer, server wins.
- Client must preserve local tombstone semantics (`isDeleted`) during merge.
- Conflict event increments metric and optional user-visible indicator.

## Idempotency and retries

- Push requests should be safely retryable by using transaction IDs as natural idempotency keys.
- Pull is naturally idempotent by timestamp cursor and upsert semantics.

## Common failure scenarios

- Token expires mid-sync: pause flow, refresh token, resume once.
- Partial push acceptance: apply partial state updates and schedule retry for failed items.
- Cursor drift due to clock issues: rely only on server-provided timestamp.
- Large payload timeout: split push batches (for example 200 items per request).

## Timeouts

- Push request timeout: 45s.
- Pull request timeout: 30s.
- Entire sync session soft budget: 90s.

## Test cases

- Offline writes then online sync.
- Conflict where local newer but server wins.
- Partial accepted + conflicts in same push response.
- Retry after 503 and network timeout.
