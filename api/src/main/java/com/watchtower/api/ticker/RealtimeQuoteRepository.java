package com.watchtower.api.ticker;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RealtimeQuoteRepository extends JpaRepository<RealtimeQuote, RealtimeQuote.Id> {

    Optional<RealtimeQuote> findFirstByIdTickerOrderByIdQuoteTimestampDesc(String ticker);
}
