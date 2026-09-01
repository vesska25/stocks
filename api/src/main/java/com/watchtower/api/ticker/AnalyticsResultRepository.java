package com.watchtower.api.ticker;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalyticsResultRepository extends JpaRepository<AnalyticsResult, Long> {

    Optional<AnalyticsResult> findFirstByTickerOrderByPriceDateDescComputedAtDesc(String ticker);
}
