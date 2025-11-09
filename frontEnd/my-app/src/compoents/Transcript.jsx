import React, { useRef, useState } from 'react';
import { pipeline } from '@xenova/transformers';

let transcriber = null;
async function loadModel() {
  if (!transcriber) {
    console.log("Loading Whisper model...");
    transcriber = await pipeline("automatic-speech-recognition", "Xenova/whisper-tiny");
    console.log("Model loaded ✅");
  }
  return transcriber;
}

export default function WhisperTranscriber() {
  const [recording, setRecording] = useState(false);
  const [status, setStatus] = useState("Ready");
  const [transcript, setTranscript] = useState("");
  const mediaRecorderRef = useRef(null);
  const chunksRef = useRef([]);

  async function startRecording() {
    setTranscript("");
    setStatus("🔴 Recording...");
    setRecording(true);

    const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
    const mimeType = MediaRecorder.isTypeSupported("audio/webm;codecs=opus")
      ? "audio/webm;codecs=opus"
      : "audio/webm";
    const mr = new MediaRecorder(stream, { mimeType });
    mediaRecorderRef.current = mr;
    chunksRef.current = [];

    mr.ondataavailable = (e) => {
      if (e.data.size > 0) chunksRef.current.push(e.data);
    };
    mr.start();
  }

  async function stopRecording() {
    setStatus("⏳ Processing...");
    setRecording(false);

    const mr = mediaRecorderRef.current;
    if (!mr) return;
    mr.stop();

    mr.onstop = async () => {
      const blob = new Blob(chunksRef.current, { type: "audio/webm" });
      const arrayBuffer = await blob.arrayBuffer();

      try { 
        const whisper = await loadModel();
        const output = await whisper(arrayBuffer);
        console.log("Output:", output);
        setTranscript(output.text);
        setStatus("✅ Done");
      } catch (err) {
        console.error(err);
        setStatus("❌ Error: " + err.message);
      }
    };
  }

  return (
    <div style={{ fontFamily: "sans-serif", maxWidth: 700, margin: "auto" }}>
      <h2>🎧 Whisper 前端语音转文字 Demo</h2>
      <p>{status}</p>
      <div style={{ marginBottom: "12px" }}>
        {!recording ? (
          <button onClick={startRecording}>Start</button>
        ) : (
          <button onClick={stopRecording}>Stop</button>
        )}
      </div>

      <div
        style={{
          border: "1px solid #ccc",
          padding: "12px",
          minHeight: "120px",
          whiteSpace: "pre-wrap",
          background: "#f8f8f8",
        }}
      >
        {transcript || "（停止录音后会自动显示识别结果）"}
      </div>
    </div>
  );
}