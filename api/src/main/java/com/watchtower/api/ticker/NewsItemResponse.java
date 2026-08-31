package com.watchtower.api.ticker;

import java.time.OffsetDateTime;

public record NewsItemResponse(
        Long id,
        String headline,
        String summary,
        String source,
        OffsetDateTime newsDatetime) {

    static NewsItemResponse from(TickerNews n) {
        return new NewsItemResponse(n.getId(), n.getHeadline(), n.getSummary(), n.getSource(), n.getNewsDatetime());
    }
}
