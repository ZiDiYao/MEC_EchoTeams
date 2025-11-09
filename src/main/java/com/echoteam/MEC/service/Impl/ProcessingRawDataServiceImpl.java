// File: com/echoteam/MEC/service/Impl/ProcessingRawDataServiceImpl.java
package com.echoteam.MEC.service.Impl;

import com.echoteam.MEC.common.LLMCode;
import com.echoteam.MEC.dto.AnalysisRequest;
import com.echoteam.MEC.dto.NormalizedAnalysisRequest;
import com.echoteam.MEC.service.ProcessingRawDataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * Service implementation responsible for normalizing raw incoming analysis requests.
 * Responsibilities:
 *  - Generate a unique requestId
 *  - Clean and normalize input data (transcript, phone)
 *  - Apply configured defaults (LLM)
 */
@Service
public class ProcessingRawDataServiceImpl implements ProcessingRawDataService {

    @Value("${mec.llm:CHATGPT}")
    private String llmConfigured;

    @Override
    public NormalizedAnalysisRequest normalize(AnalysisRequest req) {
        if (req == null) throw new IllegalArgumentException("AnalysisRequest cannot be null.");
        if (isBlank(req.getTranscript())) throw new IllegalArgumentException("Transcript cannot be blank.");
        if (isBlank(req.getPhoneNumber())) throw new IllegalArgumentException("Phone number cannot be blank.");

        String transcript = req.getTranscript().trim();
        String normalizedPhone = normalizePhoneNumber(req.getPhoneNumber());
        LLMCode llm = resolveLlm(llmConfigured);

        long epochMillis = Instant.now().toEpochMilli();
        String requestId = (normalizedPhone.isEmpty() ? "NO_PHONE" : normalizedPhone) + "_" + epochMillis;

        Timestamp timeReported = (req.getTimeReported() != null)
                ? req.getTimeReported()
                : Timestamp.from(Instant.now());

        NormalizedAnalysisRequest out = new NormalizedAnalysisRequest();
        out.setRequestId(requestId);
        out.setTranscript(transcript);
        out.setPhoneNumber(normalizedPhone);
        out.setTimeReported(timeReported);
        out.setLlmService(llm);
        return out;
    }

    private String normalizePhoneNumber(String rawPhone) {
        String digits = rawPhone == null ? "" : rawPhone.replaceAll("[^0-9]", "");
        if (digits.length() == 10 && !digits.startsWith("1")) {
            digits = "1" + digits;
        }
        return digits.isEmpty() ? "" : "+" + digits;
    }

    private LLMCode resolveLlm(String input) {
        try {
            return LLMCode.valueOf((input == null ? "" : input).trim().toUpperCase());
        } catch (Exception ignored) {
            return LLMCode.CHATGPT;
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
