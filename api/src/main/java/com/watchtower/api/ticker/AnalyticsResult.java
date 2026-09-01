package com.watchtower.api.ticker;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Maps to the existing {@code analytics_results} table. Read-only. */
@Entity
@Table(name = "analytics_results")
public class AnalyticsResult {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "ticker")
    private String ticker;

    @Column(name = "price_date")
    private LocalDate priceDate;

    @Column(name = "close")
    private BigDecimal close;

    @Column(name = "sma20")
    private BigDecimal sma20;

    @Column(name = "sma50")
    private BigDecimal sma50;

    @Column(name = "sma90")
    private BigDecimal sma90;

    @Column(name = "sma200")
    private BigDecimal sma200;

    @Column(name = "ema12")
    private BigDecimal ema12;

    @Column(name = "ema26")
    private BigDecimal ema26;

    @Column(name = "rsi14")
    private BigDecimal rsi14;

    @Column(name = "macd")
    private BigDecimal macd;

    @Column(name = "macd_signal")
    private BigDecimal macdSignal;

    @Column(name = "macd_histogram")
    private BigDecimal macdHistogram;

    @Column(name = "volume_ratio_20d")
    private BigDecimal volumeRatio20d;

    @Column(name = "high52w")
    private BigDecimal high52w;

    @Column(name = "low52w")
    private BigDecimal low52w;

    @Column(name = "position_in_range")
    private BigDecimal positionInRange;

    @Column(name = "composite_score")
    private BigDecimal compositeScore;

    /** Raw JSON text — passed through to the API response as-is (see {@code @JsonRawValue} on the DTO). */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "signals", columnDefinition = "jsonb")
    private String signals;

    /** Real column is `timestamp without time zone` — LocalDateTime, not OffsetDateTime. */
    @Column(name = "computed_at")
    private LocalDateTime computedAt;

    protected AnalyticsResult() {
        // JPA
    }

    public String getTicker() {
        return ticker;
    }

    public LocalDate getPriceDate() {
        return priceDate;
    }

    public BigDecimal getClose() {
        return close;
    }

    public BigDecimal getSma20() {
        return sma20;
    }

    public BigDecimal getSma50() {
        return sma50;
    }

    public BigDecimal getSma90() {
        return sma90;
    }

    public BigDecimal getSma200() {
        return sma200;
    }

    public BigDecimal getEma12() {
        return ema12;
    }

    public BigDecimal getEma26() {
        return ema26;
    }

    public BigDecimal getRsi14() {
        return rsi14;
    }

    public BigDecimal getMacd() {
        return macd;
    }

    public BigDecimal getMacdSignal() {
        return macdSignal;
    }

    public BigDecimal getMacdHistogram() {
        return macdHistogram;
    }

    public BigDecimal getVolumeRatio20d() {
        return volumeRatio20d;
    }

    public BigDecimal getHigh52w() {
        return high52w;
    }

    public BigDecimal getLow52w() {
        return low52w;
    }

    public BigDecimal getPositionInRange() {
        return positionInRange;
    }

    public BigDecimal getCompositeScore() {
        return compositeScore;
    }

    public String getSignals() {
        return signals;
    }

    public LocalDateTime getComputedAt() {
        return computedAt;
    }
}
