package com.seowon.coding.service;

import com.seowon.coding.domain.model.ProcessingStatus;
import com.seowon.coding.domain.repository.ProcessingStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 진행률 관리를 위한 별도 서비스
 * Self-invocation 문제를 해결하기 위해 분리
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProgressService {

    private final ProcessingStatusRepository processingStatusRepository;

    /**
     * 새로운 트랜잭션으로 진행률 업데이트
     * 부모 트랜잭션과 독립적으로 커밋되어 다른 사용자가 실시간 조회 가능
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateProgress(String jobId, int processed, int total) {
        ProcessingStatus ps = processingStatusRepository.findByJobId(jobId)
                .orElseThrow(() -> new IllegalStateException("ProcessingStatus not found for jobId: " + jobId));
        
        ps.updateProgress(processed, total);
        // 더티 체킹으로 자동 저장 (save() 불필요)
        
        log.debug("Progress updated: {}/{} for jobId: {}", processed, total, jobId);
    }

}

