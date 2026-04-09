# 05 - Notification Capture and Parser

## Goal

Auto-create transactions from supported banking/e-wallet Android notifications.

## Implementation steps

1. Build `PatternRegistry` in shared module with `PatternRule` list by package.
2. Implement pure parser API returning success/failure reason.
3. Add unit tests with real sample payloads per provider.
4. Build Android `NotificationListenerService` filter by known package names.
5. Parse notification body and map to domain transaction.
6. Save locally with `isAutoCapture=true`, `syncStatus=PENDING`.
7. Show confirmation notification and optional in-app snackbar.

## Failure scenarios

- Permission revoked: show disabled state in Settings and deep-link to notification access settings.
- Missing notification text fields: log parse skip and continue.
- Regex false positive: add provider-specific safety checks and confidence threshold.
- Duplicate capture: dedupe by `(packageName, normalizedTextHash, minuteBucket)`.

## Timeout and performance

- Parsing must be local and under 50ms per notification.
- DB insert budget under 100ms on normal device.
- If processing backlog exists, queue with bounded worker and drop oldest non-critical events.

## Testing

- Unit tests for each regex pattern and edge cases.
- Instrumented test for listener service enabled/disabled transitions.
- Regression tests for amount parsing separators (`.` and `,`).
