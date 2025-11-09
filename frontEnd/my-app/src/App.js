// src/App.js
import { useEffect, useState } from "react";
import "./App.css";
import "./index.css";
import Note from "./components/Note";
import Transcript from "./components/Transcript";
import Alsummary from "./components/AIsummary";
import Sidebar from "./components/Sidebar";
import useRecorder from "./components/useRecorder";

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
import VoiceBars from "./components/voiceBar";

function useRecorder() {
  const [recState, setRecState] = useState("idle");
  const [mediaRecorder, setMediaRecorder] = useState(null);
  const [audioBlob, setAudioBlob] = useState(null);

  const start = async () => {
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
    const mr = new MediaRecorder(stream);
    const chunks = [];
    mr.ondataavailable = (e) => e.data.size && chunks.push(e.data);
    mr.onstop = () => {
      const blob = new Blob(chunks, { type: "audio/webm" });
      setAudioBlob(blob);
    };
    mr.start();
    setMediaRecorder(mr);
    setRecState("recording");
  };

  const stop = () => {
    if (mediaRecorder) {
      mediaRecorder.stop();
      mediaRecorder.stream.getTracks().forEach((t) => t.stop());
      setRecState("stopped");
    }
  };

  const resume = () => {
    if (mediaRecorder && mediaRecorder.state === "paused") {
      mediaRecorder.resume();
      setRecState("recording");
    }
  };

  return { recState, audioBlob, start, stop, resume };
}

function Sidebar({ items, activeId, onSelect, onNew }) {
  return (
    <aside className="sidebar">
      <div className="sidebar-header">Conversations</div>
      <button className="new-btn" onClick={onNew}>+ New</button>
      <div className="sidebar-list">
        {items.length === 0 && <div className="empty">No history yet</div>}
        {items.map((it) => (
          <button
            key={it.id}
            className={`sidebar-item ${it.id === activeId ? "active" : ""}`}
            onClick={() => onSelect(it.id)}
          >
            {it.title || "Untitled"}
          </button>
        ))}
      </div>
    </aside>
  );
}

export default function App() {
  const [note, setNote] = useState("");
  const [transcript, setTranscript] = useState("");
  const [summary, setSummary] = useState("");
  const [showSummary, setShowSummary] = useState(false);
  const [isSubmitted, setIsSubmitted] = useState(false);

  const [sessions, setSessions] = useState(() => {
    const raw = localStorage.getItem("mec_sessions");
    return raw ? JSON.parse(raw) : [];
  });
  const [activeId, setActiveId] = useState(() => sessions[0]?.id || null);

  const { recState, audioBlob, start, stop, resume } = useRecorder();
  const [loading, setLoading] = useState(false);

  const [darkMode, setDarkMode] = useState(() => {
    return localStorage.getItem("darkMode") === "true";
  });

  useEffect(() => {
    localStorage.setItem("darkMode", darkMode);
    document.body.classList.toggle("dark", darkMode);
  }, [darkMode]);

  const USE_MOCK = true;

  const handleSubmit = async () => {
    setLoading(true);
    try {
      let data;
      if (USE_MOCK) {
        const resp = await fetch("/mock/submit.json");
        data = await resp.json();
      } else {
        const resp = await fetch("/api/submit", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ note, transcript }),
        });
        if (!resp.ok) throw new Error(`HTTP ${resp.status}`);
        data = await resp.json();
      }
      applyResult(data);
    } catch (e) {
      console.error(e);
      alert(`Submit failed.\n\n${e.message}\n(See console/Network for details)`);
    } finally {
      setLoading(false);
    }
  };

  function applyResult(data) {
    setSummary(data.summary || "No summary from backend.");
    const id = crypto.randomUUID();
    const title = data.title || `Case ${new Date().toLocaleString()}`;
    const newItem = { id, title, note, transcript, summary: data.summary || "" };
    setSessions((prev) => [newItem, ...prev]);
    setActiveId(id);
    setIsSubmitted(true);
    setShowSummary(true); // 直接打开弹窗
  }

  const handleLoadFromHistory = (id) => {
    setActiveId(id);
    const s = sessions.find((x) => x.id === id);
    if (!s) return;
    setNote(s.note || "");
    setTranscript(s.transcript || "");
    setSummary(s.summary || "");
    setIsSubmitted(!!s.summary);
    setShowSummary(!!s.summary); // 打开查看
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

          {/* 左侧空隙占位，保证左右对齐美观，可按需移除 */}
          <div style={{ flex: 1 }} />

          <div>
            <VoiceBars />
          </div>
          <div className="actions">
            {recState === "recording" ? (
              <button className="danger" onClick={stop} disabled={loading}>Stop</button>
            ) : recState === "paused" ? (
              <button onClick={resume} disabled={loading}>Resume</button>
            ) : (
              <button onClick={start} disabled={loading}>Record</button>
            )}

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

            {/* Submit 后显示 Summary 入口（弹窗） */}
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
            <VoiceBarPlaceholder />
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

        {/* 音频预览（可保留） */}
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
