# API Plan - POST /auth/login

## Purpose

Authenticate user and issue access/refresh tokens.

## Implementation steps

1. Validate request body.
2. Load user by email.
3. Compare password via BCrypt check.
4. Issue tokens and return profile.
5. Emit audit event (without secrets).

## Error handling

- `400`: invalid payload.
- `401`: invalid credentials.
- `429`: too many failed attempts.
- `500`: internal failure.

## Timeout/retry

- Client timeout: 15s.
- No automatic retry on failed login.

## Risks and mitigations

- Brute force: progressive backoff and temporary lockout.
- Timing attacks: constant-time compare path.

## Tests

- Success login.
- Wrong password.
- Unknown email.
- Rate limit threshold behavior.
