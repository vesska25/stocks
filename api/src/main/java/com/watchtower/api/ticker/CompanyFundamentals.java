package com.watchtower.api.ticker;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Maps to the existing {@code company_fundamentals} table. Read-only. */
@Entity
@Table(name = "company_fundamentals")
public class CompanyFundamentals {

    @EmbeddedId
    private Id id;

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
    private BigDecimal fundamentalsScore;

    /** Raw JSON text — passed through to the API response as-is. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "fundamentals_signals", columnDefinition = "jsonb")
    private String fundamentalsSignals;

    @Column(name = "forward_eps_fetched_at")
    private OffsetDateTime forwardEpsFetchedAt;

    @Column(name = "eps_surprise_fetched_at")
    private OffsetDateTime epsSurpriseFetchedAt;

    @Column(name = "fetched_at")
    private OffsetDateTime fetchedAt;

    protected CompanyFundamentals() {
        // JPA
    }

    public String getTicker() {
        return id.ticker;
    }

    public LocalDate getReportDate() {
        return id.reportDate;
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

    public BigDecimal getFundamentalsScore() {
        return fundamentalsScore;
    }

    public String getFundamentalsSignals() {
        return fundamentalsSignals;
    }

    public OffsetDateTime getFetchedAt() {
        return fetchedAt;
    }

    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "ticker")
        private String ticker;

        @Column(name = "report_date")
        private LocalDate reportDate;

        protected Id() {
            // JPA
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Id id)) return false;
            return Objects.equals(ticker, id.ticker) && Objects.equals(reportDate, id.reportDate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ticker, reportDate);
        }
    }
}
