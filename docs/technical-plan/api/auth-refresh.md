# API Plan - POST /auth/refresh

## Purpose

Rotate refresh token and issue new access token.

## Implementation steps

1. Validate refresh token payload.
2. Verify signature, expiry, issuer/audience.
3. Check token revocation/rotation state.
4. Issue new token pair.
5. Invalidate previous refresh token reference.

## Error handling

- `400`: malformed token payload.
- `401`: invalid, revoked, or expired refresh token.
- `500`: signing/storage failure.

## Timeout/retry

- Client timeout: 15s.
- Single retry allowed only for transient network errors.

## Risks and mitigations

- Replay risk: rotate and revoke old refresh token.
- Refresh stampede: client-side mutex for one active refresh call.

## Tests

- Refresh success then old token invalid.
- Expired refresh token.
- Concurrent refresh requests.
