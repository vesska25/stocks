# Stock Research Portfolio Monitor

_Started 2026-08-30 16:03 UTC_

---

## User

Design a mobile Android app for personal stock research and portfolio monitoring.

CONTEXT
The backend is already built: an automated pipeline (n8n + PostgreSQL) tracks a
watchlist of 38-200 US stocks across tech, biotech, and industrial/robotics sectors.
It pulls real-time prices (every 20 min), historical daily prices, technical
indicators, company fundamentals, and news — then computes two independent scores
per ticker and generates a daily AI digest. The app is a new REST API + mobile
frontend on top of this existing data.

DATA AVAILABLE PER TICKER
- Real-time price, % change, last update timestamp
- Technical composite_score (-3 to +7): derived from moving averages (SMA20/50/90/200),
  EMA crossover, RSI, MACD, volume vs 20-day average, and position within 52-week range
- Fundamentals_score: PE, PB, profit margin, and revenue growth compared to the
  average of same-industry peers within the watchlist (not the whole market)
- Forward EPS estimate (next fiscal year) and last-4-quarters EPS surprise history
- Top recent news headlines, filtered for relevance to that specific company
- A short AI-written analysis: why the ticker is flagged, what to watch, key news

EXISTING DISTRIBUTION
Currently delivered via a Telegram bot: one daily digest message (a curated list of
tickers whose composite_score crossed a threshold, with AI commentary), plus instant
alerts when a ticker's price moves ≥3% within 20 minutes.

WHAT THE APP SHOULD DO
v1 (this design): give the user everything the Telegram digest gives, but as a
proper mobile UI — browsable, not a wall of text. Needs:
1. A watchlist/dashboard screen: all tracked tickers, sortable/filterable by score,
   with price, % change, and both scores visible at a glance
2. A ticker detail screen: price chart, both scores broken down into their
   underlying signals, fundamentals, EPS data, and recent relevant news
3. A digest/history screen: past daily digests, so the user can scroll back
v2 (later, not needed for this design pass): push notifications for the price-spike
alerts, replacing or complementing Telegram.

USER
A solo developer/investor using this personally for research — not for trading
execution, no buy/sell buttons. Clean, data-dense, quick to scan. Dark mode friendly
(financial/trading app aesthetic), not playful or consumer-casual.

Please propose screen layouts, navigation structure, and key UI components
(score visualization, list/card design for tickers, chart style).

## Assistant



## Assistant

_[tool: ask_user]_

Waiting on your answers — mainly the design system, nav structure, and score-visualization picks.

## User

Answered — Asked for follow-up questions; design_system: Picked a design system (now attached); nav: Single scrolling home with digest inline, detail pushed on top; home_priority: Biggest movers; score_viz: option_2; chart: Sparkline-clean line + range chips (1D/1W/1M/1Y); list_density: Compact table rows; variations: Digest screen format; extras: дизайн должен быть простой и интуитивный

## Assistant



## Assistant

_[tool: ask_user]_

Round two is up — language, home block order, and which digest formats to prototype.

## User

Answered — ui_language: English; home_sections: Movers → today's digest → full watchlist table; digest_format: Timeline of days, each expandable; watchlist_size: ~38 now

## Assistant

_[tool: list_files]_

_[tool: read_file]_

_[tool: read_file]_

_[tool: copy_starter_component]_

_[tool: grep]_

Design system is Industry (steel-blue blueprint wireframe). I'll adapt its grammar — square hairline frames, registration marks, Barlow Condensed — onto a dark trading ground.

_[tool: dc_write]_

_[tool: ready_for_verification]_

Out for review. Three pieces: design notes + assumptions, a live prototype frame (1a — sort headers, filter chips, movers, digest card, ticker detail with expandable signal breakdown, digest timeline), and a component/nav spec board (1b).

Two things I decided that you may want to overrule: gain/loss is bright-steel vs dimmed-neutral rather than green/red, to stay inside Industry's mono palette; and `fundamentals_score` is drawn as a 0–100 peer percentile.

## User

Found issues — fixing…

## Assistant

_[tool: dc_js_str_replace]_

_[tool: ready_for_verification]_

Watchlist rows now populate and are tappable.

Two stated deviations from Industry: IBM Plex Mono for tabular figures (system specifies Barlow only — say the word and I'll switch to Barlow with `tabular-nums`), and inline dark-ground tokens rather than the light `styles.css` classes, since the brief called for a dark trading aesthetic.

## User

Address these comments from my teammates:

**Sergio**:
<teammate-comment>background in black I do not like. Change it 
</teammate-comment>

## Assistant

_[tool: run_script]_

_[tool: run_script]_

_[tool: grep]_

_[tool: read_file]_

_[tool: dc_html_str_replace]_

_[tool: ready_for_verification]_

Switched off black: the app now sits on Industry's light steel ground (#f7f7f8 screen, hairline steel rules, accent #416180). Up/down inverted accordingly — deep steel for gains, light neutral for losses.

## User

no no, not white and not black. Find something what is more matching to the rest of the colors

## Assistant

Steel-tinted mid ground instead — accent-200 range, with steel ink rather than neutral black.

_[tool: run_script]_

_[tool: show_html]_

_[tool: ready_for_verification]_

The screen ground is now a steel tint (#dae5ef, Industry's accent-200 range) with steel ink (#1d2d3d) and steel-tinted hairlines — it sits between the paper canvas and the accent instead of being white or black.

## User

no, bring black back, it was better

## Assistant

_[tool: grep]_

_[tool: run_script]_

_[tool: grep]_

_[tool: dc_html_str_replace]_

_[tool: ready_for_verification]_

Back to the dark ground (#101315) with bright-steel gains and dim-neutral losses, exactly as the first version.

## User

the outer and inner should have different blues

## Assistant

_[tool: run_script]_

_[tool: grep]_

_[tool: run_script]_

_[tool: ready_for_verification]_

Rings now read as two blues: outer technical in pale steel #94bce3, inner fundamentals in a saturated azure #4d8fd6 — legend swatches and the spec board updated to match.

## User

if the stock price goes down, make it darg red in the triangle

## Assistant

_[tool: run_script]_

_[tool: run_script]_

_[tool: ready_for_verification]_

Down moves now carry a dark-red ▼ (#c2503f) while the number itself stays dim neutral — in the movers strip, watchlist rows, detail header, and the digest timeline. Note this is the one non-steel colour in the design.

## User

Found issues — fixing…

## Assistant

_[tool: run_script]_

_[tool: grep]_

_[tool: read_file]_

_[tool: dc_html_str_replace]_

_[tool: ready_for_verification]_

Fixed both: the digest column now uses the same treatment as everywhere else (▲/▼ glyph coloured, number neutral), and the colour-discipline note reads correctly with the red ▼ stated as the one deviation.

