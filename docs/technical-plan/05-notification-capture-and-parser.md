# 05 - Notification Capture and Parser

## Goal

Auto-create transactions from supported banking/e-wallet Android notifications.

## Target providers (MVP)

**Priority:** Vietcombank (VCB)
- Package name: `com.VCB` (verify on device)
- Credit notification: `"So TK 00xxxxx tai VCB: +1,000,000 VND luc 10/04/2026 15:30. So du: 5,000,000 VND. Ref: CT123456789"`
- Debit notification: `"TK 1234567890 (VCB) -500,000d. SD: 2,000,000d. ND: THANH TOAN DIEN THOAI"`

**Subsequent providers:** BIDV, Techcombank, MBBank, ACB, TPBank, MoMo, ZaloPay, VNPay, ShopeePay

## Vietnamese amount format

- Thousands separator: `.` or `,` (e.g., `1.000.000` or `1,000,000`)
- Currency markers: `VND`, `d`, `đ`, `dong`
- Amounts are always whole numbers (no fractional dong)
- Parser must: strip separators, remove currency markers, convert to `Long`
- Sign detection: `+` prefix = income, `-` prefix = expense
- Example normalization: `"1.000.000 VND"` → `1000000L`, `"-500,000d"` → `500000L` (expense)

## Implementation steps

1. Build `PatternRegistry` in shared module with `PatternRule` list by package.
2. Implement pure parser API returning success/failure reason.
3. Add unit tests with real sample payloads per provider.
4. Build Android `NotificationListenerService` filter by known package names.
5. Parse notification body and map to domain transaction.
6. Save locally with `isAutoCapture=true`.
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
- Unit tests with real Vietcombank notification text samples (credit and debit).
- Vietnamese separator normalization tests (`"1.000.000"`, `"1,000,000"`, `"500000"`).
- Currency marker stripping tests (`"VND"`, `"d"`, `"đ"`).
- Instrumented test for listener service enabled/disabled transitions.
- Regression tests for amount parsing separators (`.` and `,`).
