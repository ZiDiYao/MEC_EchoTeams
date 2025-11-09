// src/components/Transcript.jsx
import React, { useRef, useState } from 'react';
import { pipeline, env} from '@xenova/transformers';
env.allowLocalModels = false;
env.useBrowserCache = false;
console.log("Transformers version:", require("@xenova/transformers/package.json").version);


let transcriber = null;
async function loadModel() {
  if (!transcriber) {
    console.log("Loading Whisper model...");
    transcriber = await pipeline('automatic-speech-recognition', 'Xenova/whisper-base.en');
    console.log("Model loaded ✅");
  }
  return transcriber;
}


export default function Transcript({ text, onChange, placeholder }) {
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

      //decode to PCM
      const audioContext = new AudioContext();
      const audioBuffer = await audioContext.decodeAudioData(arrayBuffer);

      // subsampliing to 16kHz
      const offlineCtx = new OfflineAudioContext(1, audioBuffer.duration * 16000, 16000);
      const source = offlineCtx.createBufferSource();
      source.buffer = audioBuffer;
      source.connect(offlineCtx.destination);
      source.start(0);
      const resampled = await offlineCtx.startRendering();

      const pcm = resampled.getChannelData(0);
      

      try { 
        const whisper = await loadModel();
        const output = await whisper(pcm);
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
    <div>
      <h2>🎧 Whisper 前端语音转文字 Demo</h2>
      <p>{status}</p>
      <div style={{ marginBottom: "12px" }}>
        {!recording ? (
          <button onClick={startRecording}>Start</button>
        ) : (
          <button onClick={stopRecording}>Stop</button>
        )}
      </div>
      <textarea
        className="big-input"
        value={transcript}
        onChange={(e) => onChange(e.target.value)}
        placeholder={"Transcript will appear here when recording is stopped."}
      />
    </div>
    
  );
}
