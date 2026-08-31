package com.watchtower.api.ticker;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TickerNewsRepository extends JpaRepository<TickerNews, Long> {

    /**
     * news_datetime is nullable (schema allows it), so a plain
     * OrderByNewsDatetimeDesc would push not-yet-timestamped rows to
     * arbitrary positions. Falls back to fetched_at for those.
     */
    @Query("SELECT n FROM TickerNews n WHERE n.ticker = :ticker "
            + "ORDER BY COALESCE(n.newsDatetime, n.fetchedAt) DESC")
    List<TickerNews> findByTickerOrderedByEffectiveTime(@Param("ticker") String ticker);
}
