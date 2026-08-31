package com.watchtower.api.digest;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DigestTickerSnapshotRepository extends JpaRepository<DigestTickerSnapshot, Long> {

    List<DigestTickerSnapshot> findByDigestIdIn(Collection<Long> digestIds);
}
