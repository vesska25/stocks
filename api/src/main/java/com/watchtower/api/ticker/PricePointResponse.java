package com.watchtower.api.ticker;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PricePointResponse(LocalDate date, BigDecimal close) {

    static PricePointResponse from(HistoricalPrice p) {
        return new PricePointResponse(p.getPriceDate(), p.getClose());
    }
}
