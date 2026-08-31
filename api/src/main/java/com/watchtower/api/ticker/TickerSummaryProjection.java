package com.watchtower.api.ticker;

import java.math.BigDecimal;

/**
 * Interface projection for the native "latest row per ticker" query in
 * {@link TickerRepository#findTickerSummaries()}. Getter names must match the
 * column aliases in that query.
 */
public interface TickerSummaryProjection {

    String getTicker();

    String getName();

    String getIndustry();

    BigDecimal getPrice();

    BigDecimal getChangePct();

    BigDecimal getCompositeScore();

    BigDecimal getFundamentalsScore();
}
