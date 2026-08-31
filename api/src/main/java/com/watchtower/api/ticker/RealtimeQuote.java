package com.watchtower.api.ticker;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Maps to the existing {@code realtime_quotes} table. Read-only.
 *
 * <p>This is an append-only time series (a new row roughly every 20 minutes
 * per ticker), not one row per ticker — so the composite (ticker,
 * quote_timestamp) is the identity, not ticker alone. Using ticker alone as
 * the JPA id would let Hibernate's session-level identity map collapse
 * distinct rows for the same ticker into one, silently returning a stale or
 * wrong quote.
 */
@Entity
@Table(name = "realtime_quotes")
public class RealtimeQuote {

    @EmbeddedId
    private Id id;

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

    protected RealtimeQuote() {
        // JPA
    }

    public String getTicker() {
        return id.ticker;
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
        return id.quoteTimestamp;
    }

    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "ticker")
        private String ticker;

        @Column(name = "quote_timestamp")
        private OffsetDateTime quoteTimestamp;

        protected Id() {
            // JPA
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Id id)) return false;
            return Objects.equals(ticker, id.ticker) && Objects.equals(quoteTimestamp, id.quoteTimestamp);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ticker, quoteTimestamp);
        }
    }
}
