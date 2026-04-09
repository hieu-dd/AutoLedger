# 06 - Background Sync Jobs

## Goal

Run periodic and trigger-based sync while respecting battery and network constraints.

## Android plan

- Use WorkManager periodic job every 6 hours.
- Constraints: network connected, battery not low.
- Add randomized initial delay to reduce synchronized spikes.

## iOS plan

- Use BGTaskScheduler with earliest begin date and network requirement.
- Fallback to app foreground trigger if BG task is deferred by OS.

## Trigger matrix

- App launch if last sync > 15 minutes.
- Manual sync from Settings.
- Pull-to-refresh on transaction list/dashboard.
- Background periodic trigger.

## Failure handling

- Network unavailable: reschedule with exponential backoff.
- Auth failure: stop retry loop and require foreground re-auth.
- Server 5xx: retry with capped attempts.
- Job timeout: persist progress and continue next run.

## Timeout budget

- Single background attempt hard budget: 90 seconds.
- If exceeded, cancel safely and keep pending state unchanged.

## Verification

- Simulate no network, airplane mode, and low battery states.
- Verify no duplicate concurrent sync sessions.
