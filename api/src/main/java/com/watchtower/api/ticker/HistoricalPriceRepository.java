package com.watchtower.api.ticker;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricalPriceRepository extends JpaRepository<HistoricalPrice, Long> {

    List<HistoricalPrice> findByTickerAndPriceDateGreaterThanEqualOrderByPriceDateAsc(String ticker, LocalDate from);
}
