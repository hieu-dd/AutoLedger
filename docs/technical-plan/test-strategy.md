# Test Strategy

## Test layers

- Unit tests (`shared`): parser, use cases, repository mapping, sync merge logic.
- Integration tests (`server`): auth endpoints, sync endpoints, DB transactions.
- UI tests (`composeApp`): form validation, screen state transitions, navigation paths.
- Instrumented tests (`androidMain`): notification listener behavior and permission states.

## Critical scenarios

- Offline CRUD then later sync success.
- Token expiry during sync and refresh recovery.
- Push with mixed accepted/conflict records.
- Soft delete + undo + sync propagation.
- Parser false-positive and dedupe handling.

## Failure injection plan

- Simulate 401, 429, 500, 503, and timeouts.
- Simulate DB unavailable and migration failure.
- Simulate network flapping during background sync.

## Exit criteria

- No blocker or critical bugs in auth, sync, and transaction flows.
- p95 sync latency and timeout metrics within target.
- Parser precision and recall acceptable on sample corpora.
