// File: com/echoteam/MEC/dto/NormalizedAnalysisRequest.java
package com.echoteam.MEC.dto;

import com.echoteam.MEC.common.LLMCode;
import java.sql.Timestamp;

/**
 * Normalized request after preprocessing & enrichment.
 */
public class NormalizedAnalysisRequest {

    private String requestId;
    private String transcript;
    private String phoneNumber;
    private Timestamp timeReported;
    private LLMCode llmService;

    public NormalizedAnalysisRequest() {}

    public NormalizedAnalysisRequest(String requestId, String transcript, String phoneNumber,
                                     Timestamp timeReported, LLMCode llmService) {
        this.requestId = requestId;
        this.transcript = transcript;
        this.phoneNumber = phoneNumber;
        this.timeReported = timeReported;
        this.llmService = llmService;
    }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getTranscript() { return transcript; }
    public void setTranscript(String transcript) { this.transcript = transcript; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Timestamp getTimeReported() { return timeReported; }
    public void setTimeReported(Timestamp timeReported) { this.timeReported = timeReported; }

    public LLMCode getLlmService() { return llmService; }
    public void setLlmService(LLMCode llmService) { this.llmService = llmService; }
}
