package com.watchtower.api.digest;

import com.fasterxml.jackson.annotation.JsonRawValue;
import java.time.OffsetDateTime;
import java.util.List;

public record DigestSummaryResponse(
        Long id,
        String digestText,
        @JsonRawValue String tickers,
        List<TickerSnapshotResponse> tickerSnapshots,
        OffsetDateTime createdAt) {

    static DigestSummaryResponse from(DigestHistory d, List<TickerSnapshotResponse> snapshots) {
        return new DigestSummaryResponse(d.getId(), d.getDigestText(), d.getTickers(), snapshots, d.getCreatedAt());
    }
}
