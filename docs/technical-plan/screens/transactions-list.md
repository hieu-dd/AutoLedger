# Screen Plan - /transactions

## Goal

List transactions with filters, search, pagination, and safe delete flow.

## Steps

1. Query paged local list sorted by date desc.
2. Add filter state: date range, category, type, auto-capture flag.
3. Add keyword search on merchant/notes.
4. Implement swipe-to-delete as soft delete + undo snackbar.
5. Mark changed records as `PENDING` for sync.

## Error and timeout handling

- Local DB query/write errors: show inline error and rollback optimistic UI.
- Undo timeout expiry: finalize soft delete.
- Sync error after delete: keep pending marker and retry later.

## UX safeguards

- Debounce search input.
- Preserve filter state across rotation/navigation.
- Show empty state with clear next action.

## Tests

- Filter combinations, search behavior, undo delete, large list pagination.
