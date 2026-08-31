# watchtower-api

Read-only Spring Boot REST API over the existing Postgres database populated by
the n8n pipeline. This service does not write to, migrate, or own the schema.

## Run

Set connection details via environment variables (never hardcode credentials):

```
export DB_URL=jdbc:postgresql://<host>:5432/<database>
export DB_USERNAME=<username>
export DB_PASSWORD=<password>
export API_KEY=<a long random string>
```

Then:

```
./mvnw spring-boot:run
```

`SERVER_PORT` defaults to 8080; override it the same way if needed. `API_KEY`
has no default — the app refuses to start without it rather than silently
running unprotected.

## Auth

Every endpoint requires the `X-API-Key` header to match `API_KEY`. Missing or
wrong key → 401. There's no user/session concept — single static key, single
client (the Android app), which is the right scope for a personal project;
no JWT, no accounts, no expiry.

```
curl -H "X-API-Key: $API_KEY" http://localhost:8080/api/tickers
```

The key is checked via header only, never a query parameter (query strings
end up in server access logs).

## Endpoints

### `GET /api/tickers`

Every ticker in the watchlist (`company_profile`), each joined to its latest
row in `realtime_quotes`, `analytics_results`, and `company_fundamentals`.

```json
[
  {
    "ticker": "NVDA",
    "name": "NVIDIA Corp",
    "industry": "TECH",
    "price": 184.22,
    "changePct": 2.41,
    "compositeScore": 6,
    "fundamentalsScore": 88
  }
]
```

A `null` field means that ticker has no row yet in the corresponding source
table (e.g. a ticker added to `company_profile` before its first quote or
analytics run lands).

### `GET /api/tickers/{ticker}`

Full detail for one ticker: latest quote, latest technicals (`analytics_results`,
including the raw `signals` jsonb), and latest fundamentals (`company_fundamentals`,
including `eps_surprise_last4` and `fundamentals_signals` jsonb). 404 if the
ticker isn't in `company_profile`. Any of `quote`/`technicals`/`fundamentals`
can be `null` if that ticker has no row yet in the corresponding table.

### `GET /api/tickers/{ticker}/news`

News from `ticker_news`, newest first (`news_datetime`, falling back to
`fetched_at` when null). 404 if the ticker isn't in `company_profile`.

An empty array is the expected common case, not an error: `ticker_news` is
only populated for tickers whose `composite_score` passed the daily
threshold (>=5).

### `GET /api/tickers/{ticker}/history?range={1D|1W|1M|1Y}`

Daily closes from `historical_prices` for the chart. Defaults to `1M`. Note:
`historical_prices` is daily-only (no intraday data), so `1D` is an
approximation — it returns the last two available daily closes rather than a
true intraday line.

### `GET /api/digests`

Paginated, newest first, from `digest_history`. Standard Spring pagination
params: `page` (0-based), `size` (default 20), `sort`.

```json
{
  "content": [
    {
      "id": 214,
      "digestText": "Momentum rotated into robotics: three names crossed SMA50 on above-average volume.",
      "tickers": ["SYM", "ISRG", "NVDA"],
      "tickerSnapshots": [
        {"ticker": "SYM", "compositeScore": 5, "fundamentalsScore": 52, "realtimePrice": 41.86},
        {"ticker": "ISRG", "compositeScore": 5, "fundamentalsScore": 74, "realtimePrice": 562.40},
        {"ticker": "NVDA", "compositeScore": 6, "fundamentalsScore": 88, "realtimePrice": 184.22}
      ],
      "createdAt": "2026-08-30T07:40:00Z"
    }
  ],
  "totalElements": 214,
  "totalPages": 11,
  "number": 0,
  "size": 20
}
```

`tickers` is the bare symbol array from `digest_history.tickers`.
`tickerSnapshots` is the richer per-ticker data from `digest_ticker_snapshot`
(joined on `digest_id`) — this is what backs the design's expandable
per-ticker digest rows (score/price as captured at digest time, not current
values). A ticker can appear in `tickers` without a matching snapshot if no
snapshot row was recorded for it; `tickerSnapshots` is `[]` in that case, not
an error.

## Verification

Endpoints have been mechanically verified against a local, throwaway Postgres
instance seeded with this exact schema (not against the real production
database — this sandbox has no network path to it). Verify against real data
by running the jar with real `DB_URL`/`DB_USERNAME`/`DB_PASSWORD`.
