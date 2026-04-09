# Screen Plan - /auth/register

## Goal

Register account with validation and resilient submission flow.

## Steps

1. Build form state: displayName, email, password, confirmPassword.
2. Validate password policy and confirmation match.
3. Call register use case.
4. Store token pair and user profile.
5. Navigate to `/dashboard`.

## Error and timeout handling

- 400 validation errors: map field-specific errors.
- 409 duplicate email: focused message on email field.
- Timeout/network: non-blocking retry prompt.

## UX safeguards

- Disable submit while loading.
- Keep all typed values except password on server auth errors.

## Tests

- Successful registration, duplicate email, weak password, timeout.
