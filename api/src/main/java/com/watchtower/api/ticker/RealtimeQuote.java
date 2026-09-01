package com.watchtower.api.ticker;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Maps to the existing {@code realtime_quotes} table. Read-only.
 *
 * <p>This is an append-only time series (a new row roughly every 20 minutes
 * per ticker) identified by a surrogate {@code id}, not by (ticker,
 * quote_timestamp) — that pair has no uniqueness constraint in the real
 * schema and quote_timestamp is nullable, so it can't safely be the JPA id.
 */
@Entity
@Table(name = "realtime_quotes")
public class RealtimeQuote {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "ticker")
    private String ticker;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "change")
    private BigDecimal change;

    @Column(name = "change_pct")
    private BigDecimal changePct;

    @Column(name = "high")
    private BigDecimal high;

    @Column(name = "low")
    private BigDecimal low;

    @Column(name = "open")
    private BigDecimal open;

    @Column(name = "previous_close")
    private BigDecimal previousClose;

    @Column(name = "quote_timestamp")
    private OffsetDateTime quoteTimestamp;

    protected RealtimeQuote() {
        // JPA
    }

    public String getTicker() {
        return ticker;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getChange() {
        return change;
    }

    public BigDecimal getChangePct() {
        return changePct;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public BigDecimal getPreviousClose() {
        return previousClose;
    }

    public OffsetDateTime getQuoteTimestamp() {
        return quoteTimestamp;
    }
}
