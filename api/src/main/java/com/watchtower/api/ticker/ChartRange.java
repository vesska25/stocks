package com.watchtower.api.ticker;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Chart range chips from the design (1D/1W/1M/1Y). {@code historical_prices}
 * is daily-only, so "1D" is an approximation (see
 * {@link TickerDetailService#getHistory}) rather than true intraday data.
 */
public enum ChartRange {
    ONE_DAY(2),
    ONE_WEEK(7),
    ONE_MONTH(30),
    ONE_YEAR(365);

    private final int lookbackDays;

    ChartRange(int lookbackDays) {
        this.lookbackDays = lookbackDays;
    }

    public int lookbackDays() {
        return lookbackDays;
    }

    public static ChartRange fromParam(String raw) {
        return switch (raw) {
            case "1D" -> ONE_DAY;
            case "1W" -> ONE_WEEK;
            case "1M" -> ONE_MONTH;
            case "1Y" -> ONE_YEAR;
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "range must be one of 1D, 1W, 1M, 1Y");
        };
    }
}
