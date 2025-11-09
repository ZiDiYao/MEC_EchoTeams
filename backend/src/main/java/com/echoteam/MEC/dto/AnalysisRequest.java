package com.echoteam.MEC.dto;

import java.time.Instant;

public class AnalysisRequest {

    private String transcript;
    private String phoneNumber;
    private Instant timeReported;

    public AnalysisRequest() {}

    public AnalysisRequest(String transcript, String phoneNumber, Instant timeReported) {
        this.transcript = transcript;
        this.phoneNumber = phoneNumber;
        this.timeReported = timeReported;
    }

    public String getTranscript() { return transcript; }
    public void setTranscript(String transcript) { this.transcript = transcript; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Instant getTimeReported() { return timeReported; }
    public void setTimeReported(Instant timeReported) { this.timeReported = timeReported; }
}