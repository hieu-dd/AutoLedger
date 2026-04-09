# Screen Plan - /dashboard

## Goal

Show monthly summary and recent transactions with local-first behavior.

## Steps

1. Load local summary and recent list immediately.
2. Trigger background sync if stale > 15 minutes.
3. Recompute summary when local DB updates.
4. Show pending sync badge.
5. Provide quick action to add transaction.

## Error and timeout handling

- Local query failure: show error state + retry local load.
- Sync timeout: keep local data visible and show sync warning.
- Partial sync failure: show counts and continue rendering.

## UX safeguards

- Distinguish loading states: initial empty, refreshing, and cached-data mode.
- Avoid full-screen blocking spinner after first paint.

## Tests

- Cached render first, then background refresh update.
- Sync fail while local render remains intact.
