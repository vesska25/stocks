package com.watchtower.api.ticker;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

/**
 * Maps to the existing {@code ticker_news} table. Read-only.
 *
 * <p>Only populated for tickers whose composite_score passed the daily
 * threshold (&gt;=5) — most tickers legitimately have zero rows on most days.
 */
@Entity
@Table(name = "ticker_news")
public class TickerNews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ticker")
    private String ticker;

    @Column(name = "headline")
    private String headline;

    @Column(name = "summary")
    private String summary;

    @Column(name = "source")
    private String source;

    @Column(name = "news_datetime")
    private OffsetDateTime newsDatetime;

    @Column(name = "fetched_at")
    private OffsetDateTime fetchedAt;

    protected TickerNews() {
        // JPA
    }

    public Long getId() {
        return id;
    }

    public String getTicker() {
        return ticker;
    }

    public String getHeadline() {
        return headline;
    }

    public String getSummary() {
        return summary;
    }

    public String getSource() {
        return source;
    }

    public OffsetDateTime getNewsDatetime() {
        return newsDatetime;
    }

    public OffsetDateTime getFetchedAt() {
        return fetchedAt;
    }
}
