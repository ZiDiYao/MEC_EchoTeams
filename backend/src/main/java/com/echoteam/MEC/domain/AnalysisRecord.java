package com.echoteam.MEC.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "analysis_record",
        indexes = {
                @Index(name="idx_phone_time", columnList = "phoneNumber,timeReported DESC"),
                @Index(name="idx_request_id", columnList = "requestId", unique = true)
        })
public class AnalysisRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String requestId;

    @Column(nullable = false, length = 32)
    private String phoneNumber;

    @Column(nullable = false)
    private Instant timeReported;

    private String callerName;
    private String incidentType;

    @Column(length = 1000)
    private String incidentDescription;

    private String address;
    private String landmark;

    private Integer victimCount;

    @Column(length = 500)
    private String victimDescription;

    private String injurySeverity;

    private Boolean isConscious;
    private Boolean isBreathing;
    private Boolean isOngoing;
    private Boolean isFire;

    private Integer urgencyLevel;   // 1..100
    private Integer confidence;     // 0..100

    @ElementCollection
    @CollectionTable(name="analysis_record_keyphrases", joinColumns=@JoinColumn(name="record_id"))
    @Column(name="key_phrase")
    private List<String> keyPhrases;

    private String processedBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();


    // getter and setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Instant getTimeReported() { return timeReported; }
    public void setTimeReported(Instant timeReported) { this.timeReported = timeReported; }

    public String getCallerName() { return callerName; }
    public void setCallerName(String callerName) { this.callerName = callerName; }

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

    public Integer getConfidence() { return confidence; }
    public void setConfidence(Integer confidence) { this.confidence = confidence; }

    public List<String> getKeyPhrases() { return keyPhrases; }
    public void setKeyPhrases(List<String> keyPhrases) { this.keyPhrases = keyPhrases; }

    public String getProcessedBy() { return processedBy; }
    public void setProcessedBy(String processedBy) { this.processedBy = processedBy; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
