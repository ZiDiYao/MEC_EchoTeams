import { useEffect, useRef, useState } from "react";

export default function useRecorder() {
  const [recState, setRecState] = useState("idle"); // idle | recording | paused | stopped
  const [audioBlob, setAudioBlob] = useState(null);
  const mediaRecorderRef = useRef(null); // 用 ref 避免不必要的重渲染
  const chunksRef = useRef([]);

  // 释放资源（组件卸载或停止时）
  useEffect(() => {
    return () => {
      try {
        const mr = mediaRecorderRef.current;
        if (mr && mr.state !== "inactive") {
          mr.stream?.getTracks?.().forEach((t) => t.stop());
          mr.stop();
        }
      } catch {}
    };
  }, []);

  const start = async () => {
    if (recState === "recording") return;
    setAudioBlob(null);
    chunksRef.current = [];

    // 申请麦克风
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true });

    // 一些浏览器需要明确 mimeType
    let options = {};
    if (MediaRecorder.isTypeSupported?.("audio/webm")) {
      options.mimeType = "audio/webm";
    }

    const mr = new MediaRecorder(stream, options);

    mr.ondataavailable = (e) => {
      if (e.data && e.data.size > 0) {
        chunksRef.current.push(e.data);
      }
    };

    mr.onstop = () => {
      const blob = new Blob(chunksRef.current, { type: "audio/webm" });
      setAudioBlob(blob);
    };

    mediaRecorderRef.current = mr;
    mr.start();
    setRecState("recording");
  };

  const pause = () => {
    const mr = mediaRecorderRef.current;
    if (mr && mr.state === "recording") {
      mr.pause();
      setRecState("paused");
    }
  };

  const resume = () => {
    const mr = mediaRecorderRef.current;
    if (mr && mr.state === "paused") {
      mr.resume();
      setRecState("recording");
    }
  };

  const stop = () => {
    const mr = mediaRecorderRef.current;
    if (!mr) return;
    try {
      if (mr.state !== "inactive") {
        mr.stop();
      }
      mr.stream?.getTracks?.().forEach((t) => t.stop());
    } finally {
      setRecState("stopped");
    }
  };

  return { recState, audioBlob, start, pause, resume, stop };
}