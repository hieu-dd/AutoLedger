# API Plan - GET /transactions/{id}

## Purpose

Read-only endpoint returning one transaction by ID.

## Implementation steps

1. Authenticate request.
2. Validate ID format.
3. Query transaction by ID and ownership.
4. Return `{ transaction }` if found.

## Error handling

- `400`: invalid ID format.
- `401`: unauthorized.
- `403`: not allowed to access the transaction.
- `404`: transaction not found.
- `500`: query failure.

## Timeout/retry

- Client timeout: 20s.
- Retry only on transient failures.

## Tests

- Found transaction.
- Not found.
- Ownership enforcement.
