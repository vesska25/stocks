package com.watchtower.api.ticker;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TickerRepository extends JpaRepository<CompanyProfile, String> {

    /**
     * One row per ticker in the watchlist (company_profile), joined to the
     * latest row in realtime_quotes, analytics_results and
     * company_fundamentals for that ticker.
     *
     * Uses LATERAL joins rather than DISTINCT ON / window functions because
     * "latest" is defined independently per source table (quote_timestamp,
     * price_date, report_date) — a single ORDER BY can't express that across
     * three tables at once.
     */
    @Query(value = """
            SELECT
                cp.ticker            AS ticker,
                cp.name              AS name,
                cp.industry          AS industry,
                rq.price             AS price,
                rq.change_pct        AS changePct,
                ar.composite_score   AS compositeScore,
                cf.fundamentals_score AS fundamentalsScore
            FROM company_profile cp
            LEFT JOIN LATERAL (
                SELECT price, change_pct
                FROM realtime_quotes
                WHERE ticker = cp.ticker
                ORDER BY quote_timestamp DESC
                LIMIT 1
            ) rq ON true
            LEFT JOIN LATERAL (
                SELECT composite_score
                FROM analytics_results
                WHERE ticker = cp.ticker
                ORDER BY price_date DESC
                LIMIT 1
            ) ar ON true
            LEFT JOIN LATERAL (
                SELECT fundamentals_score
                FROM company_fundamentals
                WHERE ticker = cp.ticker
                ORDER BY report_date DESC
                LIMIT 1
            ) cf ON true
            ORDER BY cp.ticker
            """, nativeQuery = true)
    List<TickerSummaryProjection> findTickerSummaries();
}
