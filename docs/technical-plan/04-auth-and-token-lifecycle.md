# 04 - Auth and Token Lifecycle

## Goal

Provide secure email/password auth with short-lived access token and refresh rotation.

## Client flow

1. Login/register obtains `accessToken` and `refreshToken`.
2. Persist tokens atomically in Multiplatform Settings.
3. Attach access token in `Authorization: Bearer`.
4. On 401, execute single-flight refresh.
5. Retry original request once if refresh succeeds.
6. If refresh fails, clear session and navigate to login.

## Server flow

- Access token TTL: 15 minutes.
- Refresh token TTL: 30 days.
- Refresh token rotation: issue new refresh token and invalidate old token (or jti denylist).

## Error handling

- Invalid credentials: return 401 with stable error code.
- Expired refresh token: return 401 and require re-login.
- Refresh race (multiple requests): enforce client-side mutex to avoid token stampede.

## Timeout and retry

- Login/register timeout 15s; no automatic retry.
- Refresh timeout 15s; one retry on transient network only.

## Security controls

- BCrypt for password hashing.
- JWT signature validation and issuer/audience checks.
- Log redaction for all auth headers and bodies.

## Tests

- Successful login/register.
- Wrong password path.
- Expired access token then refresh success.
- Expired refresh token then forced logout.
