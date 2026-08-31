package com.watchtower.api.ticker;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickers")
public class TickerController {

    private final TickerService tickerService;
    private final TickerDetailService tickerDetailService;

    public TickerController(TickerService tickerService, TickerDetailService tickerDetailService) {
        this.tickerService = tickerService;
        this.tickerDetailService = tickerDetailService;
    }

    @GetMapping
    public List<TickerSummaryResponse> getTickers() {
        return tickerService.getWatchlist();
    }

    @GetMapping("/{ticker}")
    public TickerDetailResponse getTicker(@PathVariable String ticker) {
        return tickerDetailService.getDetail(ticker.toUpperCase());
    }

    @GetMapping("/{ticker}/history")
    public List<PricePointResponse> getTickerHistory(
            @PathVariable String ticker, @RequestParam(defaultValue = "1M") String range) {
        return tickerDetailService.getHistory(ticker.toUpperCase(), ChartRange.fromParam(range));
    }

    @GetMapping("/{ticker}/news")
    public List<NewsItemResponse> getTickerNews(@PathVariable String ticker) {
        return tickerDetailService.getNews(ticker.toUpperCase());
    }
}
