# watchtower (Android)

Native Kotlin + Jetpack Compose client for the Watchtower stock-research API
(`/api` in the repo root), implementing the "Watchtower" design from the
Claude Design handoff bundle (`project/Stock Research App.dc.html`).

## Status: build-verified, running against real data

Originally written in a sandboxed environment with no Android SDK, so the
first version was hand-reviewed only, not compiled. It has since been built
and run for real (Android Studio, emulator) against the live production API
and database, and several real bugs found that way have been fixed:

- A wrong Compose import and a navigation `popUpTo` misuse (caught in
  hand-review, before the first real build).
- `enableEdgeToEdge()` with no inset padding, which put top-left touch
  targets (the detail screen's back button) under the status bar — taps
  landed on the wrong pixels while the OS back gesture kept working,
  which was the actual symptom reported. Fixed with `safeDrawingPadding()`
  once at the NavHost root.
- Settings had no way back except Save — fine for the forced first-run
  flow, wrong when opened via Home's gear icon just to check/edit existing
  values. Back button now shows only when there's an actual previous
  screen.
- Long industry names (e.g. "Life Sciences Tools & Services") wrapped to
  two lines and broke the filter-chip row height — now truncated with
  ellipsis.

## Run

1. Open `android-app/` in Android Studio, let it sync Gradle.
2. Run on an emulator or device.
3. First launch goes to a **Settings screen** (base URL + API key aren't
   hardcoded) — enter your `watchtower-api` instance's URL and the
   `API_KEY` you set on the server, then Save.
   - Emulator talking to a server on your host machine: use `10.0.2.2`
     instead of `127.0.0.1`/`localhost`, e.g. `http://10.0.2.2:8080`.
   - Physical device: needs an address actually reachable from the phone.
4. Settings are stored in DataStore, editable any time via the gear icon on
   Home (now with a back button) — no rebuild needed to point at a
   different server.

## What's implemented

- **Home** — biggest-movers strip, latest-digest preview card, sortable/
  filterable watchlist table. Sector filter chips are derived from whatever
  `industry` values come back from `/api/tickers` (not hardcoded).
- **Ticker detail** — price/change header, sparkline chart with a real
  dashed 50-day SMA overlay (see below), 1D/1W/1M/1Y range chips, dual
  score ring with expandable technical-signal breakdown, fundamentals
  (self-values), forward EPS, news.
- **Digest history** — paginated (loads more as you scroll), expandable per
  digest to show `tickerSnapshots` (score/price at digest time).
- **Settings** — base URL + API key, DataStore-backed, with a back button
  when reached from Home (not on the forced first-run screen).

Dual score ring, sparkline, and hairline row separators are custom `Canvas`
composables reproducing the design's visual language (no chart library).
Fonts are the real Barlow / Barlow Condensed / IBM Plex Mono (OFL-licensed,
bundled under `res/font`), not system stand-ins.

### The SMA50 overlay is real, not decorative

`/api/tickers/{ticker}/history` only returns raw closes, and
`analytics_results.sma50` is a single latest value, not a series — so
there's no shortcut from the API. Instead, the detail screen always fetches
a full year of closes once (`DetailViewModel.loadAll`, `range=1Y`,
regardless of which range chip is selected) and computes a genuine 50-day
simple moving average client-side (`DetailUiState.sma50`), then slices both
the raw line and the SMA line to whatever range chip is active — switching
ranges is instant (no re-fetch) since it's just re-slicing already-fetched
data. If a ticker has under 50 days of history, `sma50` comes back empty and
no overlay is drawn (rather than drawing a misleading short-window average).

## Known gaps / deliberate simplifications

- **Fundamentals show self-values only** (P/E, P/B, revenue growth YoY,
  profit margin) — no peer-comparison bars. `fundamentals_signals`/
  `eps_surprise_last4` are arbitrary JSON from the n8n pipeline with no
  confirmed shape (unlike `analytics_results.signals`, which was verified
  against the design brief) — building a bar chart on an unconfirmed
  contract risks silently rendering garbage. Get a real sample of
  `fundamentals_signals` JSON from the production DB before building this.
- **No push notifications** — v2 in the original design brief, out of scope.
- **No unit/instrumented tests.**

## Architecture

- Manual DI (`WatchtowerApplication` holds the singletons) — no Hilt,
  matches the API's "single client, personal project" scope.
- MVVM: one `ViewModel` + `StateFlow<UiState>` per screen, plain Compose
  `viewModelFactory { initializer { ... } }` (no SavedStateHandle needed —
  the only screen argument, the ticker symbol, comes from the nav route).
- Retrofit + kotlinx.serialization, with one `OkHttp` interceptor
  (`ApiKeyInterceptor`) that rewrites the request's scheme/host/port from
  DataStore settings on every call and attaches `X-API-Key` — since the
  base URL is user-configurable at runtime, not fixed at build time.
