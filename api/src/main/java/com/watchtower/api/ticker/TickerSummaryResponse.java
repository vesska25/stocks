package com.watchtower.api.ticker;

import java.math.BigDecimal;

public record TickerSummaryResponse(
        String ticker,
        String name,
        String industry,
        BigDecimal price,
        BigDecimal changePct,
        BigDecimal compositeScore,
        BigDecimal fundamentalsScore) {

    static TickerSummaryResponse from(TickerSummaryProjection p) {
        return new TickerSummaryResponse(
                p.getTicker(),
                p.getName(),
                p.getIndustry(),
                p.getPrice(),
                p.getChangePct(),
                p.getCompositeScore(),
                p.getFundamentalsScore());
    }
}
