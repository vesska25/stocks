package com.watchtower.api.ticker;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Maps to the existing {@code company_fundamentals} table. Read-only. */
@Entity
@Table(name = "company_fundamentals")
public class CompanyFundamentals {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "ticker")
    private String ticker;

    @Column(name = "report_date")
    private LocalDate reportDate;

    @Column(name = "pe_ratio")
    private BigDecimal peRatio;

    @Column(name = "pb_ratio")
    private BigDecimal pbRatio;

    @Column(name = "revenue_growth_yoy")
    private BigDecimal revenueGrowthYoy;

    @Column(name = "profit_margin")
    private BigDecimal profitMargin;

    /** Raw JSON text — passed through to the API response as-is. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "eps_surprise_last4", columnDefinition = "jsonb")
    private String epsSurpriseLast4;

    @Column(name = "forward_eps_estimate")
    private BigDecimal forwardEpsEstimate;

    @Column(name = "fundamentals_score")
    private Integer fundamentalsScore;

    /** Raw JSON text — passed through to the API response as-is. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "fundamentals_signals", columnDefinition = "jsonb")
    private String fundamentalsSignals;

    protected CompanyFundamentals() {
        // JPA
    }

    public String getTicker() {
        return ticker;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public BigDecimal getPeRatio() {
        return peRatio;
    }

    public BigDecimal getPbRatio() {
        return pbRatio;
    }

    public BigDecimal getRevenueGrowthYoy() {
        return revenueGrowthYoy;
    }

    public BigDecimal getProfitMargin() {
        return profitMargin;
    }

    public String getEpsSurpriseLast4() {
        return epsSurpriseLast4;
    }

    public BigDecimal getForwardEpsEstimate() {
        return forwardEpsEstimate;
    }

    public Integer getFundamentalsScore() {
        return fundamentalsScore;
    }

    public String getFundamentalsSignals() {
        return fundamentalsSignals;
    }
}
