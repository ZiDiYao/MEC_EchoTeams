package com.echoteam.MEC.dto;

import com.echoteam.MEC.common.LLMCode;
import java.time.Instant;

public class NormalizedAnalysisRequest {

    private String requestId;
    private String transcript;
    private String phoneNumber;
    private Instant timeReported;
    private LLMCode llmService;

    public NormalizedAnalysisRequest() {}

    public NormalizedAnalysisRequest(String requestId, String transcript, String phoneNumber,
                                     Instant timeReported, LLMCode llmService) {
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

    public Instant getTimeReported() { return timeReported; }
    public void setTimeReported(Instant timeReported) { this.timeReported = timeReported; }

    public LLMCode getLlmService() { return llmService; }
    public void setLlmService(LLMCode llmService) { this.llmService = llmService; }
}