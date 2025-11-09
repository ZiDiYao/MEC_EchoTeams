package com.echoteam.MEC.service.Impl;
import com.echoteam.MEC.domain.AnalysisRecord;
import com.echoteam.MEC.dto.AnalysisResponse;
import com.echoteam.MEC.repo.AnalysisRecordRepository;
import com.echoteam.MEC.service.HistoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class HistoryServiceImpl implements HistoryService {
    private final AnalysisRecordRepository repo;
    public HistoryServiceImpl(AnalysisRecordRepository repo) {
        this.repo = repo;
    }
    @Override
    public void saveFromResponse(AnalysisResponse r) {
        if (r == null || r.getRequestId() == null) return;
        if (repo.findByRequestId(r.getRequestId()).isPresent()) return;
        AnalysisRecord e = new AnalysisRecord();
        e.setRequestId(r.getRequestId());
        e.setPhoneNumber(r.getPhoneNumber());
        e.setTimeReported(r.getTimeReported());
        e.setCallerName(r.getCallerName());
        e.setIncidentType(r.getIncidentType());
        e.setIncidentDescription(r.getIncidentDescription());
        e.setAddress(r.getAddress());
        e.setLandmark(r.getLandmark());
        e.setVictimCount(r.getVictimCount());
        e.setVictimDescription(r.getVictimDescription());
        e.setInjurySeverity(r.getInjurySeverity());
        e.setIsConscious(r.getIsConscious());
        e.setIsBreathing(r.getIsBreathing());
        e.setIsOngoing(r.getIsOngoing());
        e.setIsFire(r.getIsFire());
        e.setUrgencyLevel(r.getUrgencyLevel());
        e.setConfidence(r.getConfidence());
        e.setKeyPhrases(r.getKeyPhrases());
        e.setProcessedBy(r.getProcessedBy());
        repo.save(e);
    }

    @Override
    public Page<AnalysisRecord> historyByPhone(String phoneNumber, Pageable pageable) {
        return repo.findByPhoneNumberOrderByTimeReportedDesc(phoneNumber, pageable);
    }

    @Override
    public AnalysisRecord getByRequestId(String requestId) {
        return repo.findByRequestId(requestId).orElse(null);
    }

    @Override
    public Page<AnalysisRecord> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

}
