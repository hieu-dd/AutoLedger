# Screen Plan - /transactions/{id}

## Goal

View and edit one transaction with safe persistence and conflict awareness.

## Steps

1. Load transaction by ID from local DB.
2. If missing, show not-found and navigate back.
3. Enable edit mode for all fields.
4. Save updates with `updatedAt` and `syncStatus=PENDING`.
5. Reflect any sync conflict resolution after next sync cycle.

## Error and timeout handling

- Missing ID/invalid state: recover via back navigation.
- Local update failure: show retry and keep dirty form.
- Conflict after sync: display non-blocking notice that server version was applied.

## UX safeguards

- Warn about unsaved changes on back action.
- Disable save while in-flight.

## Tests

- Load existing/non-existing ID.
- Edit and save path.
- Conflict overwrite notification flow.
