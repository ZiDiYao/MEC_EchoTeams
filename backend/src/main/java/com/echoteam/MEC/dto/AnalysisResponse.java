package com.echoteam.MEC.dto;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

public class AnalysisResponse {

    private String requestId;
    private String phoneNumber;
    private String callerName;
    private Timestamp timeReported;
    private String incidentType;
    private String incidentDescription;
    private String address;
    private String landmark;
    private Integer victimCount;
    private String victimDescription;
    private String injurySeverity;
    private Boolean isConscious;
    private Boolean isBreathing;
    private Boolean isOngoing;
    private Boolean isFire;
    private Integer urgencyLevel;
    private List<String> keyPhrases;
    private int confidence;
    private String processedBy;

    // ===== Constructors =====
    public AnalysisResponse() {}

    public AnalysisResponse(
            String requestId,
            String phoneNumber,
            String callerName,
            Timestamp timeReported,
            String incidentType,
            String incidentDescription,
            String address,
            String landmark,
            Integer victimCount,
            String victimDescription,
            String injurySeverity,
            Boolean isConscious,
            Boolean isBreathing,
            Boolean isOngoing,
            Boolean isFire,
            Integer urgencyLevel,
            List<String> keyPhrases,
            int confidence,
            String processedBy) {
        this.requestId = requestId;
        this.phoneNumber = phoneNumber;
        this.callerName = callerName;
        this.timeReported = timeReported;
        this.incidentType = incidentType;
        this.incidentDescription = incidentDescription;
        this.address = address;
        this.landmark = landmark;
        this.victimCount = victimCount;
        this.victimDescription = victimDescription;
        this.injurySeverity = injurySeverity;
        this.isConscious = isConscious;
        this.isBreathing = isBreathing;
        this.isOngoing = isOngoing;
        this.isFire = isFire;
        this.urgencyLevel = urgencyLevel;
        this.keyPhrases = keyPhrases;
        this.confidence = confidence;
        this.processedBy = processedBy;
    }

    // ===== Getters and Setters =====
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getCallerName() { return callerName; }
    public void setCallerName(String callerName) { this.callerName = callerName; }

    public Timestamp getTimeReported() { return timeReported; }
    public void setTimeReported(Timestamp timeReported) { this.timeReported = timeReported; }

    public String getIncidentType() { return incidentType; }
    public void setIncidentType(String incidentType) { this.incidentType = incidentType; }

    public String getIncidentDescription() { return incidentDescription; }
    public void setIncidentDescription(String incidentDescription) { this.incidentDescription = incidentDescription; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getLandmark() { return landmark; }
    public void setLandmark(String landmark) { this.landmark = landmark; }

    public Integer getVictimCount() { return victimCount; }
    public void setVictimCount(Integer victimCount) { this.victimCount = victimCount; }

    public String getVictimDescription() { return victimDescription; }
    public void setVictimDescription(String victimDescription) { this.victimDescription = victimDescription; }

    public String getInjurySeverity() { return injurySeverity; }
    public void setInjurySeverity(String injurySeverity) { this.injurySeverity = injurySeverity; }

    public Boolean getIsConscious() { return isConscious; }
    public void setIsConscious(Boolean isConscious) { this.isConscious = isConscious; }

    public Boolean getIsBreathing() { return isBreathing; }
    public void setIsBreathing(Boolean isBreathing) { this.isBreathing = isBreathing; }

    public Boolean getIsOngoing() { return isOngoing; }
    public void setIsOngoing(Boolean isOngoing) { this.isOngoing = isOngoing; }

    public Boolean getIsFire() { return isFire; }
    public void setIsFire(Boolean isFire) { this.isFire = isFire; }

    public Integer getUrgencyLevel() { return urgencyLevel; }
    public void setUrgencyLevel(Integer urgencyLevel) { this.urgencyLevel = urgencyLevel; }

    public List<String> getKeyPhrases() { return keyPhrases; }
    public void setKeyPhrases(List<String> keyPhrases) { this.keyPhrases = keyPhrases; }

    public int getConfidence() { return confidence; }
    public void setConfidence(int confidence) { this.confidence = confidence; }

    public String getProcessedBy() { return processedBy; }
    public void setProcessedBy(String processedBy) { this.processedBy = processedBy; }

    // ===== equals & hashCode =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnalysisResponse)) return false;
        AnalysisResponse that = (AnalysisResponse) o;
        return confidence == that.confidence &&
                Objects.equals(requestId, that.requestId) &&
                Objects.equals(phoneNumber, that.phoneNumber) &&
                Objects.equals(callerName, that.callerName) &&
                Objects.equals(timeReported, that.timeReported) &&
                Objects.equals(incidentType, that.incidentType) &&
                Objects.equals(incidentDescription, that.incidentDescription) &&
                Objects.equals(address, that.address) &&
                Objects.equals(landmark, that.landmark) &&
                Objects.equals(victimCount, that.victimCount) &&
                Objects.equals(victimDescription, that.victimDescription) &&
                Objects.equals(injurySeverity, that.injurySeverity) &&
                Objects.equals(isConscious, that.isConscious) &&
                Objects.equals(isBreathing, that.isBreathing) &&
                Objects.equals(isOngoing, that.isOngoing) &&
                Objects.equals(isFire, that.isFire) &&
                Objects.equals(urgencyLevel, that.urgencyLevel) &&
                Objects.equals(keyPhrases, that.keyPhrases) &&
                Objects.equals(processedBy, that.processedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, phoneNumber, callerName, timeReported, incidentType,
                incidentDescription, address, landmark, victimCount, victimDescription,
                injurySeverity, isConscious, isBreathing, isOngoing, isFire,
                urgencyLevel, keyPhrases, confidence, processedBy);
    }

    // ===== toString =====
    @Override
    public String toString() {
        return "AnalysisResponse{" +
                "requestId='" + requestId + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", callerName='" + callerName + '\'' +
                ", timeReported=" + timeReported +
                ", incidentType='" + incidentType + '\'' +
                ", incidentDescription='" + incidentDescription + '\'' +
                ", address='" + address + '\'' +
                ", landmark='" + landmark + '\'' +
                ", victimCount=" + victimCount +
                ", victimDescription='" + victimDescription + '\'' +
                ", injurySeverity='" + injurySeverity + '\'' +
                ", isConscious=" + isConscious +
                ", isBreathing=" + isBreathing +
                ", isOngoing=" + isOngoing +
                ", isFire=" + isFire +
                ", urgencyLevel=" + urgencyLevel +
                ", keyPhrases=" + keyPhrases +
                ", confidence=" + confidence +
                ", processedBy='" + processedBy + '\'' +
                '}';
    }
}
