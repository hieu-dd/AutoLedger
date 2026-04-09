# Screen Plan - /charts

## Goal

Render category, income-vs-expense, and trend charts for selected period.

## Steps

1. Add period selector: week/month/3 months/year/custom.
2. Query aggregated data from local DB via use cases.
3. Render pie/donut, bar, and line charts.
4. Recompute on period change and data updates.

## Error and timeout handling

- Empty dataset: show friendly empty state with CTA to add transaction.
- Aggregation failure: show chart-level fallback and retry.
- Large computation: run off main thread and cache previous result.

## UX safeguards

- Keep previous chart while new period is loading.
- Normalize timezone boundaries to avoid month cutoff errors.

## Tests

- Aggregation correctness for each period.
- No-crash with empty and extreme values.
