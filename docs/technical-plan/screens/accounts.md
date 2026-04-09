# Screen Plan - /accounts

## Goal

Manage account list and balance visibility across account types.

## Steps

1. Load account list from local DB.
2. Show grouped view by account type.
3. Support add/edit/delete account operations.
4. Recompute balances when transactions change.

## Error and timeout handling

- Invalid currency or account type: validation error.
- Delete account with linked transactions: prevent hard delete, require migration strategy.
- Local write failure: revert optimistic changes.

## UX safeguards

- Confirm destructive actions.
- Show empty state for first-time setup.

## Tests

- CRUD account flows.
- Balance updates after transaction edits/deletes.
