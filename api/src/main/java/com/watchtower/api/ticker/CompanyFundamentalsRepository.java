package com.watchtower.api.ticker;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyFundamentalsRepository extends JpaRepository<CompanyFundamentals, CompanyFundamentals.Id> {

    Optional<CompanyFundamentals> findFirstByIdTickerOrderByIdReportDateDesc(String ticker);
}
