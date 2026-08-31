package com.watchtower.api.ticker;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TickerDetailResponse(
        String ticker,
        String name,
        String industry,
        Quote quote,
        Technicals technicals,
        Fundamentals fundamentals) {

    public record Quote(
            BigDecimal price,
            BigDecimal change,
            BigDecimal changePct,
            BigDecimal high,
            BigDecimal low,
            BigDecimal open,
            BigDecimal previousClose,
            OffsetDateTime quoteTimestamp) {

        static Quote from(RealtimeQuote q) {
            return new Quote(
                    q.getPrice(), q.getChange(), q.getChangePct(), q.getHigh(),
                    q.getLow(), q.getOpen(), q.getPreviousClose(), q.getQuoteTimestamp());
        }
    }

    public record Technicals(
            BigDecimal compositeScore,
            BigDecimal sma20,
            BigDecimal sma50,
            BigDecimal sma90,
            BigDecimal sma200,
            BigDecimal ema12,
            BigDecimal ema26,
            BigDecimal rsi14,
            BigDecimal macd,
            BigDecimal macdSignal,
            BigDecimal macdHistogram,
            BigDecimal volumeRatio20d,
            BigDecimal high52w,
            BigDecimal low52w,
            BigDecimal positionInRange,
            @JsonInclude(JsonInclude.Include.NON_NULL) @JsonRawValue String signals,
            OffsetDateTime computedAt) {

        static Technicals from(AnalyticsResult a) {
            return new Technicals(
                    a.getCompositeScore(), a.getSma20(), a.getSma50(), a.getSma90(), a.getSma200(),
                    a.getEma12(), a.getEma26(), a.getRsi14(), a.getMacd(), a.getMacdSignal(),
                    a.getMacdHistogram(), a.getVolumeRatio20d(), a.getHigh52w(), a.getLow52w(),
                    a.getPositionInRange(), a.getSignals(), a.getComputedAt());
        }
    }

    public record Fundamentals(
            BigDecimal peRatio,
            BigDecimal pbRatio,
            BigDecimal revenueGrowthYoy,
            BigDecimal profitMargin,
            BigDecimal forwardEpsEstimate,
            BigDecimal fundamentalsScore,
            @JsonInclude(JsonInclude.Include.NON_NULL) @JsonRawValue String epsSurpriseLast4,
            @JsonInclude(JsonInclude.Include.NON_NULL) @JsonRawValue String fundamentalsSignals) {

        static Fundamentals from(CompanyFundamentals f) {
            return new Fundamentals(
                    f.getPeRatio(), f.getPbRatio(), f.getRevenueGrowthYoy(), f.getProfitMargin(),
                    f.getForwardEpsEstimate(), f.getFundamentalsScore(), f.getEpsSurpriseLast4(),
                    f.getFundamentalsSignals());
        }
    }
}
