import React from "react";
import "../css/SummaryModel.css"; 

export default function SummaryModel({ data, onClose }) {
  if (!data) return null;

  return (
    <div className="modal-overlay">
      <div className="modal">
        <h2>📋 Incident Summary</h2>

        <div className="modal-content">
          <p><strong>Request ID:</strong> {data.requestId}</p>
          <p><strong>Caller:</strong> {data.callerName}</p>
          <p><strong>Phone:</strong> {data.phoneNumber}</p>
          <p><strong>Time Reported:</strong> {new Date(data.timeReported).toLocaleString()}</p>
          <p><strong>Type:</strong> {data.incidentType}</p>
          <p><strong>Description:</strong> {data.incidentDescription}</p>
          <p><strong>Address:</strong> {data.address}</p>
          {data.landmark && <p><strong>Landmark:</strong> {data.landmark}</p>}

          <p><strong>Victim Count:</strong> {data.victimCount}</p>
          {data.victimDescription && <p><strong>Victim Info:</strong> {data.victimDescription}</p>}
          <p><strong>Severity:</strong> {data.injurySeverity}</p>

          <p><strong>Conscious:</strong> {data.isConscious ? "Yes" : "No"}</p>
          <p><strong>Breathing:</strong> {data.isBreathing ? "Yes" : "No"}</p>
          <p><strong>Ongoing:</strong> {data.isOngoing ? "Yes" : "No"}</p>
          <p><strong>Fire Involved:</strong> {data.isFire ? "Yes" : "No"}</p>

          <p><strong>Urgency:</strong> {data.urgencyLevel}</p>
          <p><strong>Confidence:</strong> {data.confidence}%</p>

          {data.keyPhrases && data.keyPhrases.length > 0 && (
            <p><strong>Key Phrases:</strong> {data.keyPhrases.join(", ")}</p>
          )}

          <p><strong>Processed By:</strong> {data.processedBy}</p>
        </div>

        <button className="close-btn" onClick={onClose}>Close</button>
      </div>
    </div>
  );
}
