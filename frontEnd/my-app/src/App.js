// src/App.js
import { useEffect, useMemo, useState } from "react";
import "./App.css";
import "./index.css";
import Note from "./components/Note";
import Transcript from "./components/Transcript";
import Alsummary from "./components/AIsummary";
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
  const [tab, setTab] = useState("summary");
  const [note, setNote] = useState("");
  const [transcript, setTranscript] = useState("");
  const [summary, setSummary] = useState("");

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

  const handleSubmit = async () => {
    setLoading(true);
    try {
      const resp = await fetch("/api/submit", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ note, transcript }),
      });
      const data = await resp.json();
      setSummary(data.summary || "No summary from backend.");
      const id = crypto.randomUUID();
      const title = data.title || `Case ${new Date().toLocaleString()}`;
      const newItem = { id, title, note, transcript, summary: data.summary || "" };
      setSessions((prev) => [newItem, ...prev]);
      setActiveId(id);
      setTab("summary");
    } catch (e) {
      console.error(e);
      alert("Submit failed.");
    } finally {
      setLoading(false);
    }
  };

  const handleLoadFromHistory = (id) => {
    setActiveId(id);
    const s = sessions.find((x) => x.id === id);
    if (!s) return;
    setNote(s.note || "");
    setTranscript(s.transcript || "");
    setSummary(s.summary || "");
    setTab("summary");
  };

  const handleNew = () => {
    setActiveId(null);
    setNote("");
    setTranscript("");
    setSummary("");
    setTab("summary");
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
          <div className="tabs">
            <button
              className={`tab ${tab === "summary" ? "active" : ""}`}
              onClick={() => setTab("summary")}
            >
              Summary
            </button>
            <button
              className={`tab ${tab === "note" ? "active" : ""}`}
              onClick={() => setTab("note")}
            >
              Note
            </button>
            <button
              className={`tab ${tab === "transcript" ? "active" : ""}`}
              onClick={() => setTab("transcript")}
            >
              Transcript
            </button>
          </div>
          <div>
            <VoiceBars />
          </div>
          <div className="actions">
            {recState === "recording" ? (
              <button className="danger" onClick={stop}>Stop</button>
            ) : recState === "paused" ? (
              <button onClick={resume}>Resume</button>
            ) : (
              <button onClick={start}>Record</button>
            )}

            <button className="primary" onClick={handleSubmit} disabled={loading}>
              {loading ? "Loading..." : "Submit"}
            </button>

            {/* 夜间模式切换 */}
            <button
              className="toggle-theme"
              onClick={() => setDarkMode((v) => !v)}
              title="Toggle dark mode"
            >
              {darkMode ? "Day Mode" : "Night Mode"}
            </button>
          </div>
        </header>

        <section className="panel">
          {tab === "summary" && <Alsummary text={summary} />}
          {tab === "note" && (
            <Note value={note} onChange={setNote} placeholder="911 switchboard operator could write note here" />
          )}
          {tab === "transcript" && (
            <Transcript
              text={transcript}
              onChange={(t) => setTranscript(t)}
              placeholder="Here will display the text transcripted by record"
            />
          )}
        </section>
        {/* 测试用音频播放器 */}
        {audioBlob && (
          <div style={{ padding: "10px 16px" }}>
            <p>🎧 录音预览：</p>
            <audio controls src={URL.createObjectURL(audioBlob)} />
          </div>
        )}

      </main>
    </div>
  );
}
