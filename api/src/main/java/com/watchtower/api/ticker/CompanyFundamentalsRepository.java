package com.watchtower.api.ticker;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyFundamentalsRepository extends JpaRepository<CompanyFundamentals, Long> {

    Optional<CompanyFundamentals> findFirstByTickerOrderByReportDateDesc(String ticker);
}
