# API Plan - POST /auth/register

## Purpose

Create user account and return token pair + user profile.

## Request/response

- Request body: `{ email, password, displayName }`.
- Success: `201` with `{ accessToken, refreshToken, user }`.

## Implementation steps

1. Validate payload schema and normalize email.
2. Enforce password policy and displayName length.
3. Check unique email.
4. Hash password with BCrypt and create user row.
5. Issue JWT access and refresh tokens.
6. Return response with user object.

## Error handling

- `400`: invalid payload, weak password, invalid email format.
- `409`: email already exists.
- `429`: too many attempts.
- `500`: database or token generation failure.

## Timeout/retry

- Server processing target: < 1s normal path.
- Client timeout: 15s.
- Client auto-retry: disabled.

## Risks and mitigations

- Email enumeration risk: return generic conflict text.
- Spam account creation: add per-IP and per-email rate limit.

## Tests

- Valid registration, duplicate email, weak password, malformed payload.
