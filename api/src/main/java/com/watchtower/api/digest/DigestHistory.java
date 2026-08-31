package com.watchtower.api.digest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Maps to the existing {@code digest_history} table. Read-only. */
@Entity
@Table(name = "digest_history")
public class DigestHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "digest_text")
    private String digestText;

    /** Raw JSON text (array of ticker symbols) — passed through to the API response as-is. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tickers", columnDefinition = "jsonb")
    private String tickers;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    protected DigestHistory() {
        // JPA
    }

    public Long getId() {
        return id;
    }

    public String getDigestText() {
        return digestText;
    }

    public String getTickers() {
        return tickers;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
