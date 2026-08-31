package com.watchtower.api.ticker;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricalPriceRepository extends JpaRepository<HistoricalPrice, HistoricalPrice.Id> {

    List<HistoricalPrice> findByIdTickerAndIdPriceDateGreaterThanEqualOrderByIdPriceDateAsc(
            String ticker, LocalDate from);
}
