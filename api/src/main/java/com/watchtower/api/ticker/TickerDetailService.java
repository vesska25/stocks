package com.watchtower.api.ticker;

import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TickerDetailService {

    private final TickerRepository tickerRepository;
    private final RealtimeQuoteRepository realtimeQuoteRepository;
    private final AnalyticsResultRepository analyticsResultRepository;
    private final CompanyFundamentalsRepository companyFundamentalsRepository;
    private final HistoricalPriceRepository historicalPriceRepository;
    private final TickerNewsRepository tickerNewsRepository;

    public TickerDetailService(
            TickerRepository tickerRepository,
            RealtimeQuoteRepository realtimeQuoteRepository,
            AnalyticsResultRepository analyticsResultRepository,
            CompanyFundamentalsRepository companyFundamentalsRepository,
            HistoricalPriceRepository historicalPriceRepository,
            TickerNewsRepository tickerNewsRepository) {
        this.tickerRepository = tickerRepository;
        this.realtimeQuoteRepository = realtimeQuoteRepository;
        this.analyticsResultRepository = analyticsResultRepository;
        this.companyFundamentalsRepository = companyFundamentalsRepository;
        this.historicalPriceRepository = historicalPriceRepository;
        this.tickerNewsRepository = tickerNewsRepository;
    }

    public TickerDetailResponse getDetail(String ticker) {
        CompanyProfile profile = tickerRepository.findById(ticker)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown ticker: " + ticker));

        TickerDetailResponse.Quote quote = realtimeQuoteRepository
                .findFirstByIdTickerOrderByIdQuoteTimestampDesc(ticker)
                .map(TickerDetailResponse.Quote::from)
                .orElse(null);

        TickerDetailResponse.Technicals technicals = analyticsResultRepository
                .findFirstByIdTickerOrderByIdPriceDateDescComputedAtDesc(ticker)
                .map(TickerDetailResponse.Technicals::from)
                .orElse(null);

        TickerDetailResponse.Fundamentals fundamentals = companyFundamentalsRepository
                .findFirstByIdTickerOrderByIdReportDateDesc(ticker)
                .map(TickerDetailResponse.Fundamentals::from)
                .orElse(null);

        return new TickerDetailResponse(
                profile.getTicker(), profile.getName(), profile.getIndustry(),
                quote, technicals, fundamentals);
    }

    /**
     * Daily closes for the chart. {@code historical_prices} only stores one
     * row per day, so "1D" cannot show an intraday line — it falls back to
     * the last two available daily closes instead of erroring.
     */
    public List<PricePointResponse> getHistory(String ticker, ChartRange range) {
        if (!tickerRepository.existsById(ticker)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown ticker: " + ticker);
        }
        LocalDate from = LocalDate.now().minusDays(range.lookbackDays());
        return historicalPriceRepository
                .findByIdTickerAndIdPriceDateGreaterThanEqualOrderByIdPriceDateAsc(ticker, from)
                .stream()
                .map(PricePointResponse::from)
                .toList();
    }

    /**
     * Empty list is the expected common case: ticker_news is only populated
     * for tickers that passed the daily composite_score threshold (>=5).
     */
    public List<NewsItemResponse> getNews(String ticker) {
        if (!tickerRepository.existsById(ticker)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown ticker: " + ticker);
        }
        return tickerNewsRepository.findByTickerOrderedByEffectiveTime(ticker).stream()
                .map(NewsItemResponse::from)
                .toList();
    }
}
