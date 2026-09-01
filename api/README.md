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

## Deploy with Docker (recommended over running via mvnw on a dev machine)

Running this from a Windows dev machine over an SSH tunnel to Postgres works
for local testing, but means the API is only reachable while that machine
and tunnel are up. Better: run it as a container on the same server as
Postgres/n8n, on the same Docker network, so it talks to Postgres by
container name — no tunnel needed at all.

```bash
# On the server, in a clone of this repo:
cd stocks/api

# Find the docker network n8n-postgres is already on:
docker inspect n8n-postgres --format '{{range $k,$v := .NetworkSettings.Networks}}{{$k}}{{end}}'

docker build -t watchtower-api .

# Keep secrets out of shell history / `docker inspect`:
cat > watchtower-api.env <<'EOF'
DB_URL=jdbc:postgresql://n8n-postgres:5432/stock_watchlist
DB_USERNAME=n8nuser
DB_PASSWORD=<real password>
API_KEY=<real key>
EOF
chmod 600 watchtower-api.env

# Bind to localhost only — a reverse proxy (below) handles the public side.
# Pick any free local port; the production deploy uses 8082.
docker run -d --name watchtower-api --restart unless-stopped \
  --network <network-name-from-above> \
  -p 127.0.0.1:8082:8080 \
  --env-file watchtower-api.env \
  watchtower-api
```

### HTTPS via nginx + certbot

This server already fronts other apps with system nginx + certbot (one
vhost per app in `/etc/nginx/sites-available/`, each proxying to that app's
`127.0.0.1:<port>`) rather than a container-based reverse proxy — reused
that existing pattern instead of introducing a second one (e.g. Caddy).

```bash
sudo tee /etc/nginx/sites-available/stocks > /dev/null <<'EOF'
server {
    listen 80;
    server_name stocks.yourdomain.example;

    location / {
        proxy_pass http://127.0.0.1:8082;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
EOF
sudo ln -s /etc/nginx/sites-available/stocks /etc/nginx/sites-enabled/stocks
sudo nginx -t && sudo systemctl reload nginx

# Requires a DNS A record for the domain pointing at this server first.
sudo certbot --nginx -d stocks.yourdomain.example
```

certbot rewrites the vhost in place to add the `listen 443 ssl` block, the
cert paths, and an HTTP→HTTPS redirect, and sets up its own auto-renewal —
nothing else to maintain. Point the Android app's Settings at
`https://stocks.yourdomain.example` (no port — 443 is HTTPS's default).
Works from anywhere (mobile data, any wifi), not just the same LAN as the
server, and the API key no longer travels over the wire in cleartext.

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

`fundamentals_signals` is passed through as-is (`@JsonRawValue`), so any keys
the n8n pipeline adds to that jsonb column show up here automatically with no
API change needed. As of the peer-comparison rollout it carries, alongside
the original four `+1/-1/0` flags (`peVsIndustry`, `pbVsIndustry`,
`marginVsIndustry`, `growthVsIndustry`): `industry` (the comparison group),
`peer_tickers` (other watchlist tickers in that industry), and
`industry_averages` (`pe_ratio`, `pb_ratio`, `profit_margin`,
`revenue_growth_yoy`). The whole object is `null` when `fundamentals_score`
is null (ticker is the only one in its industry, nothing to compare against).

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
instance, and separately confirmed working against the real production
database (running the API against the real `stock_watchlist` DB surfaced
one real schema mismatch, since fixed: `company_profile.name` doesn't
exist — the real column is `company_name`).

Every table in the real schema uses a surrogate `id SERIAL PRIMARY KEY`
(not composite keys on ticker+date), which the entities now match exactly —
an earlier version used composite embedded ids for `realtime_quotes`,
`historical_prices`, `analytics_results`, and `company_fundamentals` based
on an initial schema description that omitted the `id` column; that was a
real bug for `realtime_quotes` specifically, since `(ticker,
quote_timestamp)` has no uniqueness constraint there and `quote_timestamp`
is nullable, so it could have collided in Hibernate's identity map. Also
fixed: `analytics_results.computed_at` is `timestamp without time zone`
(`LocalDateTime`), not `timestamptz` (`OffsetDateTime`).
