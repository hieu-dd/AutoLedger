# 07 - Observability and Security

## Observability plan

- Structured logs (JSON) on server with `requestId`, `route`, `status`, latency.
- Client analytics events for sync start/success/failure and conflict count.
- Metrics dashboard:
  - auth success/failure ratio
  - token refresh rate
  - sync latency p50/p95
  - sync conflict rate
  - parser success rate by package

## Logging rules

- Never log password, JWT, refresh token, or full email.
- Mask sensitive fields in DTO logs.
- Include error code and stacktrace only at server side.

## Security hardening

- JWT validation with issuer, audience, expiry checks.
- Password hashing with BCrypt cost tuned for server capacity.
- Rate limiting for auth endpoints and brute-force lockout policy.
- Input validation for all request DTO fields.
- Strict CORS policy for web/admin access if needed.

## Incident readiness

- Add runbook for token compromise and refresh token revocation.
- Add operational alarms for 5xx spikes and DB pool exhaustion.
