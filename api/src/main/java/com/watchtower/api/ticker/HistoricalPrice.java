package com.watchtower.api.ticker;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/** Maps to the existing {@code historical_prices} table. Read-only. */
@Entity
@Table(name = "historical_prices")
public class HistoricalPrice {

    @EmbeddedId
    private Id id;

    @Column(name = "open")
    private BigDecimal open;

    @Column(name = "high")
    private BigDecimal high;

    @Column(name = "low")
    private BigDecimal low;

    @Column(name = "close")
    private BigDecimal close;

    @Column(name = "volume")
    private Long volume;

    protected HistoricalPrice() {
        // JPA
    }

    public String getTicker() {
        return id.ticker;
    }

    public LocalDate getPriceDate() {
        return id.priceDate;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public BigDecimal getClose() {
        return close;
    }

    public Long getVolume() {
        return volume;
    }

    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "ticker")
        private String ticker;

        @Column(name = "price_date")
        private LocalDate priceDate;

        protected Id() {
            // JPA
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Id id)) return false;
            return Objects.equals(ticker, id.ticker) && Objects.equals(priceDate, id.priceDate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ticker, priceDate);
        }
    }
}
