package com.watchtower.api.ticker;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Maps to the existing {@code historical_prices} table. Read-only. */
@Entity
@Table(name = "historical_prices")
public class HistoricalPrice {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "ticker")
    private String ticker;

    @Column(name = "price_date")
    private LocalDate priceDate;

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
        return ticker;
    }

    public LocalDate getPriceDate() {
        return priceDate;
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
}
