// src/components/Transcript.jsx
import React, { useRef, useState } from 'react';
import { pipeline, env } from '@xenova/transformers';

env.allowLocalModels = false;
env.useBrowserCache = false;
console.log("Transformers version:", require("@xenova/transformers/package.json").version);

let transcriber = null;
async function loadModel() {
  if (!transcriber) {
    console.log("Loading Whisper model...");
    transcriber = await pipeline('automatic-speech-recognition', 'Xenova/whisper-base.en');
    console.log("Model loaded");
  }
  return transcriber;
}

export default function Transcript({ text, onChange, placeholder }) {
  const [recording, setRecording] = useState(false);
  const [status, setStatus] = useState("Ready");
  const mediaRecorderRef = useRef(null);
  const chunksRef = useRef([]);

  async function startRecording() {
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
    setStatus("Processing...");
    setRecording(false);
    const mr = mediaRecorderRef.current;
    if (!mr) return;

    mr.onstop = async () => {
      try {
        const blob = new Blob(chunksRef.current, { type: "audio/webm" });
        const arrayBuffer = await blob.arrayBuffer();

        // decode -> resample 16kHz mono
        const audioContext = new AudioContext();
        const audioBuffer = await audioContext.decodeAudioData(arrayBuffer);
        const frameCount = Math.ceil(audioBuffer.duration * 16000);
        const offlineCtx = new OfflineAudioContext(1, frameCount, 16000);
        const source = offlineCtx.createBufferSource();
        source.buffer = audioBuffer;
        source.connect(offlineCtx.destination);
        source.start(0);
        const resampled = await offlineCtx.startRendering();
        await audioContext.close();

        const pcm = resampled.getChannelData(0);

        const whisper = await loadModel();
        const output = await whisper(pcm);
        console.log("Output:", output);

  
        onChange(output.text || "");
        setStatus("Done");
      } catch (err) {
        console.error(err);
        setStatus("❌ Error: " + err.message);
      } finally {
        if (mr && mr.stream) {
          mr.stream.getTracks().forEach(t => t.stop());
        }
      }
    };

    mr.stop();
  }

  return (
    <div>
      <p>{status}</p>
      <div style={{ marginBottom: 12 }}>
        {!recording ? (
          <button onClick={startRecording}>Start</button>
        ) : (
          <button onClick={stopRecording}>Stop</button>
        )}
      </div>

      <textarea
        className="big-input"
        value={text}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder || "Transcript will appear here when recording is stopped."}
      />
    </div>
  );
}
