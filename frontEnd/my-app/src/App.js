// src/App.js
import { useEffect, useState } from "react";
import "./App.css";
import "./index.css";
import Note from "./components/Note";
import Transcript from "./components/Transcript";
import AIsummary from "./components/AIsummary";
import Sidebar from "./components/Sidebar";
import useRecorder from "./components/useRecorder";
// 如果已经有 Jeff 的组件就解注释下一行，并把 USE_VOICEBARS = true
import VoiceBars from "./components/voiceBar";

/* 占位的音量条（等 Jeff 的真组件到位后替换） */
function VoiceBarPlaceholder() {
  return (
    <div className="voicebar">
      <div className="voicebar-wave" />
      <span className="voicebar-text">Voice bar (coming soon)</span>
    </div>
  );
}

/* Summary 弹窗 */
function SummaryModal({ open, onClose, text }) {
  if (!open) return null;
  return (
    <div className="overlay">
      <div className="modal">
        <div className="modal-header">
          <strong>Summary</strong>
          <button className="modal-close" onClick={onClose}>✕</button>
        </div>
        <div className="summary-box">{text || "No summary."}</div>
      </div>
    </div>
  );
}

/* Loading 覆盖层 */
function LoadingOverlay({ text = "Analyzing… please wait" }) {
  return (
    <div className="overlay" role="alert" aria-busy="true" aria-live="assertive">
      <div className="overlay-card">
        <div className="spinner" aria-hidden="true" />
        <div className="overlay-text">{text}</div>
      </div>
    </div>
  );
}

function safe(v, fallback = "Unknown") {
  if (v === undefined || v === null) return fallback;
  if (typeof v === "string" && v.trim() === "") return fallback;
  return v;
}

function formatTime(iso) {
  try { return new Date(iso).toLocaleString(); } catch { return "Unknown time"; }
}

/** 统一规整后端结果 -> { title, summary }（标题=电话+事件+时间戳） */
function normalizeResult(raw, { phoneNumber, transcript }) {
  const incidentType = raw.incidentType || raw.type || raw.category || "Case";
  const address      = raw.address || raw.location || "";
  const victims      = raw.victimCount ?? raw.victims ?? raw.peopleInvolved;
  const urgency      = raw.urgencyLevel ?? raw.urgency ?? raw.priority;
  const confidence   = raw.confidence ?? raw.score;
  const timeReported = raw.timeReported || new Date().toISOString();
  const aiSummary    = raw.summary || raw.incidentDescription || "";

  // === Title：Phone · Incident · LocalTime ===
  const phoneForTitle = safe(phoneNumber, "No-Phone");
  const timeForTitle  = formatTime(timeReported);
  const title = `${phoneForTitle} · ${incidentType} · ${timeForTitle}`;

  // === Summary：结构化要点（有则填、无则略） ===
  const lines = [
    `# ${incidentType}`,
    address ? `📍 Address: ${address}` : null,
    victims !== undefined ? `🧑‍🤝‍🧑 Victims: ${victims}` : null,
    urgency !== undefined ? `🚨 Urgency: ${urgency}` : null,
    confidence !== undefined ? `✅ Confidence: ${confidence}%` : null,
    `🕒 Reported: ${formatTime(timeReported)}`,
    "",
    "## AI Summary",
    aiSummary || "No summary from backend.",
    "",
    "## Original Transcript (short)",
    transcript?.slice(0, 400) ? transcript.slice(0, 400) + (transcript.length > 400 ? " …" : "") : "N/A",
  ].filter(Boolean);

  return { title, summary: lines.join("\n") };
}


export default function App() {
  const [phoneNumber, setPhoneNumber] = useState("");
  const [note, setNote] = useState("");
  const [transcript, setTranscript] = useState("");
  const [summary, setSummary] = useState("");
  const [showSummary, setShowSummary] = useState(false);
  const [isSubmitted, setIsSubmitted] = useState(false);

  const [sessions, setSessions] = useState(() => {
    const raw = localStorage.getItem("mec_sessions");
    return raw ? JSON.parse(raw) : [];
  });
  const [activeId, setActiveId] = useState(() => {
    const raw = localStorage.getItem("mec_sessions");
    const arr = raw ? JSON.parse(raw) : [];
    return arr[0]?.id || null;
  });

  const { recState, audioBlob, start, stop, resume } = useRecorder();
  const [loading, setLoading] = useState(false);

  const [darkMode, setDarkMode] = useState(() => {
    return localStorage.getItem("darkMode") === "true";
  });

  useEffect(() => {
    localStorage.setItem("darkMode", darkMode);
    document.body.classList.toggle("dark", darkMode);
  }, [darkMode]);

  // 会把历史记录持久化
  useEffect(() => {
    localStorage.setItem("mec_sessions", JSON.stringify(sessions));
  }, [sessions]);

  const USE_MOCK = false;
  const USE_VOICEBARS = true; // 如果已经有 Jeff 的 VoiceBars，改成 true 并引入

  const handleSubmit = async () => {
    try {
      // ===== 1️⃣ 前端校验 =====
      if (!transcript || !transcript.trim()) {
        alert("Transcript is empty. Please record or paste text before submitting.");
        return;
      }

      setLoading(true);
      let data;

      // ===== 2️⃣ MOCK 或真实请求 =====
      if (USE_MOCK) {
        const resp = await fetch("/mock/submit.json");
        data = await resp.json();
      } else {
        const resp = await fetch("/api/analyze", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            transcript,
            phoneNumber: phoneNumber || null,
            timeReported: new Date().toISOString(),
          }),
        });

        if (!resp.ok) {
          const text = await resp.text().catch(() => "");
          throw new Error(`HTTP ${resp.status}${text ? " - " + text : ""}`);
        }

        data = await resp.json(); // ✅ 这里不再用 const，避免作用域错误
      }

      console.log("[/api/analyze] response:", data);

      // ===== 3️⃣ 转换后端返回为前端展示格式 =====
      const { title, summary } = normalizeResult(data, { phoneNumber, transcript });

      // ===== 4️⃣ 更新前端状态 =====
      setSummary(summary);
      const id = crypto.randomUUID();
      const newItem = { id, title, note, transcript, summary, createdAt: new Date().toISOString() };
      setSessions((prev) => [newItem, ...prev]);
      setActiveId(id);
      setIsSubmitted(true);
      setShowSummary(true);
    } catch (e) {
      console.error(e);
      alert(`Submit failed.\n\n${e.message}\n(See console/Network for details)`);
    } finally {
      setLoading(false);
    }
  };


  function applyResult(data) {
    const { title, summary } = normalizeResult(data, { phoneNumber, transcript });
    setSummary(summary);

    const id = crypto.randomUUID();
    const newItem = { id, title, note, transcript, summary, createdAt: new Date().toISOString() };
    setSessions((prev) => [newItem, ...prev]);
    setActiveId(id);
    setIsSubmitted(true);
    setShowSummary(true);
  }

  const handleLoadFromHistory = (id) => {
    setActiveId(id);
    const s = sessions.find((x) => x.id === id);
    if (!s) return;
    setNote(s.note || "");
    setTranscript(s.transcript || "");
    setSummary(s.summary || "");
    setIsSubmitted(!!s.summary);
    setShowSummary(!!s.summary);
  };

  const handleNew = () => {
    setActiveId(null);
    setNote("");
    setTranscript("");
    setSummary("");
    setIsSubmitted(false);
    setShowSummary(false);
  };

  return (
    <div className={`app-shell ${darkMode ? "dark" : ""}`}>
      <Sidebar
        items={sessions}
        activeId={activeId}
        onSelect={handleLoadFromHistory}
        onNew={handleNew}
      />

      <main className="main">
        <header className="topbar">
          <div className="title">Generate Title</div>
          <div style={{ flex: 1 }} />

          <div>
            {USE_VOICEBARS ? (
              <><div className="voicebar-wrapper">
              <VoiceBars />
                </div> 
                <div /> 
              </> // 占位，避免未引入时报错
            ) : (
              <VoiceBarPlaceholder />
            )}
          </div>

          <div className="actions">
              <input
                type="tel"
                placeholder="Phone (optional)"
                value={phoneNumber}
                onChange={(e) => setPhoneNumber(e.target.value)}
                style={{ marginRight: 8, padding: "6px 8px", borderRadius: 6, border: "1px solid #ddd" }}
              />
            <button className="primary" onClick={handleSubmit} disabled={loading}>
              {loading ? "Loading..." : "Submit"}
            </button>

            <button
              className="toggle-theme"
              onClick={() => setDarkMode((v) => !v)}
              title="Toggle dark mode"
              disabled={loading}
            >
              {darkMode ? "Day Mode" : "Night Mode"}
            </button>

            {isSubmitted && (
              <button className="tab" onClick={() => setShowSummary(true)}>
                Summary
              </button>
            )}
          </div>
        </header>

        {/* 两栏布局：左 Note + 右 Transcript */}
        <section className="two-col">
          <div className="col left">
            {/* 如果上面 header 已经有 VoiceBar，就可以把这里的 Placeholder 删掉 */}
            <Note
              value={note}
              onChange={setNote}
              placeholder="911 switchboard operator could write note here"
            />
          </div>

          <div className="col right">
            <Transcript
              text={transcript}
              onChange={setTranscript}
              placeholder="Transcript will appear here when recording is stopped."
            />
          </div>
        </section>

        {/* 音频预览 */}
        {audioBlob && (
          <div style={{ padding: "10px 16px" }}>
            <p>🎧 录音预览：</p>
            <audio controls src={URL.createObjectURL(audioBlob)} />
          </div>
        )}

        {/* 弹窗与 Loading 覆盖层 */}
        <SummaryModal open={showSummary} onClose={() => setShowSummary(false)} text={summary} />
        {loading && <LoadingOverlay text="Analyzing audio & generating summary..." />}
      </main>
    </div>
  );
}
