package com.echoteam.MEC.service;

import com.echoteam.MEC.domain.AnalysisRecord;
import com.echoteam.MEC.dto.AnalysisResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HistoryService {
    void saveFromResponse(AnalysisResponse resp);
    Page<AnalysisRecord> historyByPhone(String phoneNumber, Pageable pageable);
    AnalysisRecord getByRequestId(String requestId);
    Page<AnalysisRecord> findAll(Pageable pageable);
}