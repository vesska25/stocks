package com.watchtower.api.digest;

import java.math.BigDecimal;

public record TickerSnapshotResponse(
        String ticker, Integer compositeScore, Integer fundamentalsScore, BigDecimal realtimePrice) {

    static TickerSnapshotResponse from(DigestTickerSnapshot s) {
        return new TickerSnapshotResponse(s.getTicker(), s.getCompositeScore(), s.getFundamentalsScore(), s.getRealtimePrice());
    }
}
