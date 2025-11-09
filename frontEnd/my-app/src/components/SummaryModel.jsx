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
          <p><strong>Type:</strong> {data.incidentType}</p>
          <p><strong>Description:</strong> {data.incidentDescription}</p>
          <p><strong>Address:</strong> {data.address}</p>
          <p><strong>Victims:</strong> {data.victimCount}</p>
          <p><strong>Severity:</strong> {data.injurySeverity}</p>
          <p><strong>Urgency:</strong> {data.urgencyLevel}</p>
          <p><strong>Processed By:</strong> {data.processedBy}</p>
        </div>

        <button className="close-btn" onClick={onClose}>Close</button>
      </div>
    </div>
  );
}
