import { useEffect, useState } from "react";
import SummaryModel from "./SummaryModel";

export default function Sidebar({ items, activeId, onSelect, onNew }) {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedReport, setSelectedReport] = useState(null); // 🆕 保存点击的记录详情

  useEffect(() => {
    fetch("http://localhost:8080/api/history")
      .then((res) => res.json())
      .then((data) => {
        console.log("Fetch history success:", data);
        if (data && Array.isArray(data.content)) {
          setHistory(data.content);
        } else {
          setHistory([]);
        }
        setLoading(false);
      })
      .catch((err) => {
        console.error("❌ Fetch error:", err);
        setLoading(false);
      });
  }, []);

  async function handleSelect(id) {
    try {
      const res = await fetch(`http://localhost:8080/api/history/${id}`);
      const data = await res.json();
      console.log("📄 Fetched report:", data);
      setSelectedReport(data);
    } catch (err) {
      console.error("❌ Failed to fetch report:", err);
    }
  }

  if (loading) return <p>Loading...</p>;
  return (
    <aside className="sidebar">
      <div className="sidebar-header">History</div>
      <button className="new-btn" onClick={onNew}>+ New</button>

      <div className="sidebar-list">
        {history.length === 0 ? (
          <div className="empty">No history yet</div>
        ) : (
          history.map((record) => (
            <button
              key={record.requestId}
              className={`sidebar-item ${
                record.requestId === activeId ? "active" : ""
              }`}
              onClick={() => handleSelect(record.requestId)}
            >
              <div className="sidebar-item-title">
                {record.requestId || "Unknown ID"}
              </div>
            </button>
          ))
        )}
      </div>

      {selectedReport && (
        <SummaryModel
          data={selectedReport}
          onClose={() => setSelectedReport(null)}
        />
      )}
    </aside>
  );
}
