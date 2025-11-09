package com.echoteam.MEC.dto;

import java.sql.Timestamp;

public class AnalysisRequest {

    private String transcript;
    private String phoneNumber;
    /** Optional: time when the report was received on client side */
    private Timestamp timeReported;

    public AnalysisRequest() {}

    public AnalysisRequest(String transcript, String phoneNumber, Timestamp timeReported) {
        this.transcript = transcript;
        this.phoneNumber = phoneNumber;
        this.timeReported = timeReported;
    }

    public String getTranscript() { return transcript; }
    public void setTranscript(String transcript) { this.transcript = transcript; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Timestamp getTimeReported() { return timeReported; }
    public void setTimeReported(Timestamp timeReported) { this.timeReported = timeReported; }
}