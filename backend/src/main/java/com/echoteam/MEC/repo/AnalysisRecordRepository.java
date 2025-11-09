package com.echoteam.MEC.repo;

import com.echoteam.MEC.domain.AnalysisRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnalysisRecordRepository extends JpaRepository<AnalysisRecord, Long> {
    Page<AnalysisRecord> findByPhoneNumberOrderByTimeReportedDesc(String phoneNumber, Pageable pageable);
    Optional<AnalysisRecord> findByRequestId(String requestId);
}