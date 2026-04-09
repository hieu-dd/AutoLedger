# 01 - Cross-Cutting Error, Timeout, Retry

## Error taxonomy

- `ValidationError`: invalid user input, invalid request payload.
- `AuthError`: invalid credentials, expired token, revoked refresh token.
- `NetworkError`: no internet, DNS failure, TLS handshake issue.
- `TimeoutError`: connect timeout, read timeout, request timeout.
- `ServerError`: 5xx responses, maintenance, transient overload.
- `DataError`: migration failure, enum mismatch, corrupted local row.
- `ConflictError`: sync conflict where server is newer.

## Error payload standard (server)

```json
{
  "code": "AUTH_INVALID_CREDENTIALS",
  "message": "Invalid email or password",
  "requestId": "req-...",
  "details": {}
}
```

## Timeout matrix

- Auth endpoints (`/auth/*`): connect 10s, request 15s, socket 15s.
- Sync pull: connect 10s, request 30s, socket 30s.
- Sync push: connect 10s, request 45s, socket 45s.
- Read-only transactions APIs: connect 10s, request 20s.

## Retry policy

- Never retry on 4xx (except 401 with token refresh flow).
- Retry on transient failures: network IO and HTTP 502/503/504.
- Backoff: 500ms, 1s, 2s with jitter 0-250ms.
- Max retries:
  - Login/register: 0 automatic retry.
  - Refresh token: 1 retry on transient errors.
  - Pull sync: up to 2 retries.
  - Push sync: up to 2 retries only if request is idempotent.

## Cancellation policy

- Screen-level jobs canceled on route dispose.
- Manual sync can be canceled by user; preserve partial status.
- Background sync should stop on connectivity loss and reschedule.

## UX mapping

- Validation errors: inline field error.
- Auth errors: top form error + keep typed values except password.
- Timeout/network: non-blocking banner + retry action.
- Sync partial failure: show accepted/conflict counts and actionable message.

## Logging and observability

- Add `requestId`, `userId`, `deviceId`, and endpoint in every server log event.
- Redact PII: email local part masked, no token/password in logs.
- Metric counters: auth failure rate, sync conflict rate, timeout count.
