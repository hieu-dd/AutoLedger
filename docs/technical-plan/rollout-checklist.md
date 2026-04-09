# Rollout Checklist

## Phase 1 - Foundation

- Domain models and repository contracts implemented.
- SQLDelight schema and migrations validated.
- Local transaction CRUD works offline.

## Phase 2 - UI core routes

- All specified routes implemented with real local data.
- Validation, empty states, and loading states present.
- Swipe delete + undo complete.

## Phase 3 - Charts

- All three chart types implemented and period selector wired.
- Aggregation validated with test fixtures.

## Phase 4 - Notification capture (Android)

- Listener service registered and permission flow complete.
- Parser tests for initial providers passing.

## Phase 5 - Backend

- Auth + sync endpoints implemented with validation and status pages.
- Rate limiting and JWT security controls enabled.

## Phase 6 - Client sync

- Push-then-pull orchestration complete.
- Token refresh and retry behavior validated.
- Background sync jobs enabled on Android and iOS.

## Phase 7 - Production hardening

- Metrics and logs are live and redacted.
- Integration and regression suites stable.
- Release build and proguard rules verified.
