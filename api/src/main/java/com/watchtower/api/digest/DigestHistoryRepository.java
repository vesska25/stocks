package com.watchtower.api.digest;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DigestHistoryRepository extends JpaRepository<DigestHistory, Long> {
}
