package com.watchtower.api.digest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DigestService {

    private final DigestHistoryRepository digestHistoryRepository;
    private final DigestTickerSnapshotRepository digestTickerSnapshotRepository;

    public DigestService(
            DigestHistoryRepository digestHistoryRepository,
            DigestTickerSnapshotRepository digestTickerSnapshotRepository) {
        this.digestHistoryRepository = digestHistoryRepository;
        this.digestTickerSnapshotRepository = digestTickerSnapshotRepository;
    }

    public Page<DigestSummaryResponse> getDigests(Pageable pageable) {
        Page<DigestHistory> page = digestHistoryRepository.findAll(pageable);

        List<Long> digestIds = page.getContent().stream().map(DigestHistory::getId).toList();
        Map<Long, List<TickerSnapshotResponse>> snapshotsByDigestId = digestTickerSnapshotRepository
                .findByDigestIdIn(digestIds).stream()
                .collect(Collectors.groupingBy(
                        DigestTickerSnapshot::getDigestId,
                        Collectors.mapping(TickerSnapshotResponse::from, Collectors.toList())));

        return page.map(d -> DigestSummaryResponse.from(d, snapshotsByDigestId.getOrDefault(d.getId(), List.of())));
    }
}
