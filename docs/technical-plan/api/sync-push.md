# API Plan - POST /sync/push

## Purpose

Accept local pending transactions and resolve conflicts.

## Request/response

- Request: `{ deviceId, transactions: [...] }`.
- Response: `{ accepted: [ids], conflicts: [...], serverTimestamp }`.

## Implementation steps

1. Authenticate request.
2. Validate batch size and payload schema.
3. For each transaction ID:
   - Insert if missing.
   - Update if client version considered newer per policy.
   - Return conflict object if server version is newer.
4. Return accepted IDs, conflicts, and current server timestamp.

## Error handling

- `400`: invalid schema, oversized batch.
- `401`: missing/invalid token.
- `413`: payload too large.
- `500`: DB transaction failure.

## Timeout/retry

- Client timeout: 45s.
- Batch limit recommended: 200-500 records.
- Safe retry supported due to ID-based idempotency.

## Risks and mitigations

- Long DB transaction: chunk processing and index optimization.
- Conflict storm: surface conflict count and capture diagnostics.

## Tests

- All accepted.
- Mixed accepted/conflicts.
- Duplicate push replay.
- Oversized payload.
