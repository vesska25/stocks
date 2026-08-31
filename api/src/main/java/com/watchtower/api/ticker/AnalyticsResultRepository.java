package com.watchtower.api.ticker;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalyticsResultRepository extends JpaRepository<AnalyticsResult, AnalyticsResult.Id> {

    Optional<AnalyticsResult> findFirstByIdTickerOrderByIdPriceDateDescComputedAtDesc(String ticker);
}
