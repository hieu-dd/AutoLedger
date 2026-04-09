# Screen Plan - /auth/login

## Goal

Authenticate user with robust error handling and predictable state transitions.

## Steps

1. Build form state: email, password, isLoading, errorMessage.
2. Validate email format and non-empty password locally.
3. Submit login request via `AuthUseCase.login`.
4. Persist token pair atomically.
5. Navigate to `/dashboard` on success.

## Error and timeout handling

- Invalid fields: show inline errors.
- 401: show generic credentials error.
- Timeout/network: show retryable banner.
- 429: show temporary lockout message.

## UX safeguards

- Disable submit while request in-flight.
- Preserve email field on error; clear password if auth fails.
- Prevent double-tap submit.

## Tests

- Valid login, wrong credentials, network timeout, and button disabled during loading.
