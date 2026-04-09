# AutoLedger Technical Plan

This folder contains implementation plans per domain area, per screen route, and per API endpoint.

## Document map

- `00-overview-architecture.md`: system boundaries, module ownership, and implementation order.
- `01-cross-cutting-error-timeout-retry.md`: shared reliability standards.
- `02-data-model-and-schema.md`: domain to DB to API mapping.
- `03-sync-protocol-and-conflict.md`: push/pull contract and conflict policy.
- `04-auth-and-token-lifecycle.md`: auth flow and token rotation.
- `05-notification-capture-and-parser.md`: Android notification capture and parser design.
- `06-background-sync-jobs.md`: periodic and trigger-based sync jobs.
- `07-observability-and-security.md`: logging, metrics, security hardening.
- `screens/*.md`: plan per app route/screen.
- `api/*.md`: plan per backend endpoint.
- `test-strategy.md`: test matrix and failure injection plan.
- `rollout-checklist.md`: phased rollout and acceptance gates.
- `backlog-ordered.md`: implementation backlog tasks in execution order.

## Scope

- Local-first mobile app (KMP) with offline CRUD.
- Ktor backend for auth and sync.
- Android-only auto capture from notifications.

## Standards used in this plan

- Error model: typed domain errors + UX-safe messages.
- Timeout model: explicit connect/request/socket budgets.
- Retry model: idempotent-only retry with exponential backoff + jitter.
- Sync model: push then pull, server timestamp cursor, server wins on conflict.
