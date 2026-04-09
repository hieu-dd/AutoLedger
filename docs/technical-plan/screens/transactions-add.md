# Screen Plan - /transactions/add

## Goal

Create a transaction reliably in offline-first mode.

## Steps

1. Load selectable categories and accounts.
2. Build form state: amount, type, category, account, date, notes.
3. Validate amount > 0 and required fields.
4. Save local row with `syncStatus=PENDING`.
5. Navigate back with success event.

## Error and timeout handling

- Validation errors: inline field message.
- Local save failure: keep form values and show retry.
- Reference missing (category/account deleted): force reselection.

## UX safeguards

- Disable submit during save.
- Prevent duplicate save on rapid taps.

## Tests

- Successful save, validation failures, DB insert error handling.
