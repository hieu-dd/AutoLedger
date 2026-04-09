# API Plan - GET /sync/pull?since={epochMillis}

## Purpose

Return server-side transaction changes newer than provided cursor.

## Implementation steps

1. Authenticate request.
2. Validate `since` param (epoch millis, default 0).
3. Query transactions where `server_timestamp > since` for current user.
4. Return rows and latest `serverTimestamp`.

## Error handling

- `400`: invalid `since` parameter.
- `401`: unauthorized.
- `500`: query failure.

## Timeout/retry

- Client timeout: 30s.
- Client retry: up to 2 on transient errors.

## Risks and mitigations

- Large result set: introduce paging/cursor continuation if needed.
- Cursor bugs: always trust server-issued timestamp, not client clock.

## Tests

- First pull with since=0.
- Incremental pull with updates only.
- Invalid since format.
