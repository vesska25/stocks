package com.watchtower.api.ticker;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TickerService {

    private final TickerRepository tickerRepository;

    public TickerService(TickerRepository tickerRepository) {
        this.tickerRepository = tickerRepository;
    }

    public List<TickerSummaryResponse> getWatchlist() {
        return tickerRepository.findTickerSummaries().stream()
                .map(TickerSummaryResponse::from)
                .toList();
    }
}
