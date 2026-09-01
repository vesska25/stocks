# watchtower (Android)

Native Kotlin + Jetpack Compose client for the Watchtower stock-research API
(`/api` in the repo root), implementing the "Watchtower" design from the
Claude Design handoff bundle (`project/Stock Research App.dc.html`).

## Important: not build-verified

This was written in a sandboxed environment with no access to the Android
SDK, Google's Maven repo, or the Gradle distribution servers — so unlike
`/api`, **this has not been compiled or run**. It's been carefully
hand-reviewed for API correctness (Compose, Retrofit, DataStore, Navigation),
and a couple of real bugs were caught and fixed that way (a wrong import,
a navigation `popUpTo` misuse), but there's no substitute for an actual
build. Open it in Android Studio, sync Gradle, and report back the first
error — dependency versions or Compose/API usage are the most likely spots
for something to have slipped through.

The Gradle wrapper (`gradlew`, `gradle/wrapper/gradle-wrapper.jar`) was
generated with a real local Gradle install, so it's a genuine wrapper, not a
stub — `./gradlew` will download Gradle 8.9 and Android Gradle Plugin 8.5.2
into your Gradle cache the first time you run it (needs your machine to have
normal internet/Maven access, which this sandbox didn't).

## Run

1. Open `android-app/` in Android Studio (or `File > Open` on this
   directory), let it sync Gradle.
2. Run on an emulator or device.
3. First launch goes to a **Settings screen** (base URL + API key aren't
   hardcoded — see below) — enter your `watchtower-api` instance's URL and
   the `API_KEY` you set on the server, then Save.
   - Emulator talking to a server on your host machine: use `10.0.2.2`
     instead of `127.0.0.1`/`localhost` (Android's standard host-loopback
     alias), e.g. `http://10.0.2.2:8080`.
   - Physical device: needs an address actually reachable from the phone —
     your machine's LAN IP, or wherever the API is actually running.
4. Settings are stored in DataStore, editable any time via the gear icon on
   Home — no rebuild needed to point at a different server.

## What's implemented

- **Home** — biggest-movers strip, latest-digest preview card, sortable/
  filterable watchlist table. Sector filter chips are derived from whatever
  `industry` values come back from `/api/tickers` (not hardcoded).
- **Ticker detail** — price/change header, sparkline chart with 1D/1W/1M/1Y
  range chips, dual score ring with expandable technical-signal breakdown,
  fundamentals (self-values), forward EPS, news.
- **Digest history** — paginated (loads more as you scroll), expandable per
  digest to show `tickerSnapshots` (score/price at digest time).
- **Settings** — base URL + API key, DataStore-backed.

Dual score ring, sparkline, and hairline row separators are custom `Canvas`
composables reproducing the design's visual language (no chart library).

## Known gaps / deliberate simplifications

- **No dashed SMA50 overlay on the chart.** `/api/tickers/{ticker}/history`
  returns raw closes only; `analytics_results.sma50` is a single latest
  value, not a series aligned to the chart's date range, so overlaying it
  would be fabricated. Omitted rather than faked.
- **Fundamentals show self-values only** (P/E, P/B, revenue growth YoY,
  profit margin) — no peer-comparison bars. `fundamentals_signals`/
  `eps_surprise_last4` are arbitrary JSON from the n8n pipeline with no
  confirmed shape, so building a bar chart on them would be guessing at a
  contract that was never verified end-to-end.
- **Technical signal breakdown** (`analytics_results.signals`) IS parsed
  and rendered (`ui/detail/SignalsParsing.kt`), on the assumption it's a
  JSON array of `{label, value, pts}` objects, matching the original design
  brief. If the real column is shaped differently, that section will just
  render empty rather than crash — worth checking against real data.
- **No push notifications** — v2 in the original design brief, out of scope.
- **Fonts**: the design specifies Barlow / Barlow Condensed / IBM Plex Mono;
  this build uses system sans-serif/monospace as stand-ins (no network
  access to fetch font files here). Swap `ui/theme/Type.kt`'s `FontFamily`
  values for the real fonts under `res/font` whenever convenient.
- **No unit/instrumented tests** — the standard boilerplate test files exist
  in Gradle's expected locations but nothing meaningful was added to them,
  given the build itself couldn't be verified here.

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
