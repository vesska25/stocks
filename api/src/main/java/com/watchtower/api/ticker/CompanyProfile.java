package com.watchtower.api.ticker;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Maps to the existing {@code company_profile} table. Read-only: this API
 * never writes to tables owned by the n8n pipeline.
 */
@Entity
@Table(name = "company_profile")
public class CompanyProfile {

    @Id
    @Column(name = "ticker")
    private String ticker;

    @Column(name = "industry")
    private String industry;

    @Column(name = "company_name")
    private String name;

    protected CompanyProfile() {
        // JPA
    }

    public String getTicker() {
        return ticker;
    }

    public String getIndustry() {
        return industry;
    }

    public String getName() {
        return name;
    }
}
