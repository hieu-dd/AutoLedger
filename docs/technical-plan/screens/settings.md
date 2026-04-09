# Screen Plan - /settings

## Goal

Expose app-level controls: notification access, sync actions, theme, and logout.

## Steps

1. Show session info, last sync timestamp, pending count.
2. Add manual sync button with progress status.
3. On Android, show notification listener permission status and open settings deep link.
4. Add theme mode selector.
5. Implement logout flow to clear tokens and session state.

## Error and timeout handling

- Manual sync timeout/failure: show retry action and keep pending queue intact.
- Permission state unknown: fallback to best-effort detection and explanatory text.
- Logout local clear failure: force clear in-memory session and restart app state.

## UX safeguards

- Prevent repeated manual sync taps while running.
- Confirm logout to avoid accidental action.

## Tests

- Manual sync state transitions.
- Notification permission state rendering.
- Logout clears auth state and returns to login.
