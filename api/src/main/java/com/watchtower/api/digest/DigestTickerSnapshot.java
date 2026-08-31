package com.watchtower.api.digest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Maps to the existing {@code digest_ticker_snapshot} table. Read-only.
 *
 * <p>One row per ticker mentioned in a digest, capturing that ticker's
 * scores/price as of when the digest was generated — this is what backs the
 * design's expandable per-ticker digest rows (digest_history.tickers alone
 * is just a bare symbol array with no scores).
 */
@Entity
@Table(name = "digest_ticker_snapshot")
public class DigestTickerSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "digest_id")
    private Long digestId;

    @Column(name = "ticker")
    private String ticker;

    @Column(name = "composite_score")
    private Integer compositeScore;

    @Column(name = "fundamentals_score")
    private Integer fundamentalsScore;

    @Column(name = "realtime_price")
    private BigDecimal realtimePrice;

    @Column(name = "captured_at")
    private OffsetDateTime capturedAt;

    protected DigestTickerSnapshot() {
        // JPA
    }

    public Long getId() {
        return id;
    }

    public Long getDigestId() {
        return digestId;
    }

    public String getTicker() {
        return ticker;
    }

    public Integer getCompositeScore() {
        return compositeScore;
    }

    public Integer getFundamentalsScore() {
        return fundamentalsScore;
    }

    public BigDecimal getRealtimePrice() {
        return realtimePrice;
    }

    public OffsetDateTime getCapturedAt() {
        return capturedAt;
    }
}
