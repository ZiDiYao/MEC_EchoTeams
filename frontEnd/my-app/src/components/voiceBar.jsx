import React, { useEffect, useRef, useState } from "react";

export default function VoiceBars() {
  const [volume, setVolume] = useState(0);
  const [isSpeaking, setIsSpeaking] = useState(false);
  const analyserRef = useRef(null);
  const dataRef = useRef(null);
  const smoothVolRef = useRef(0);

  useEffect(() => {
    let animationId;
    let stream;
    let audioContext;
    let analyser;
    let dataArray;

    async function init() {
      stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      audioContext = new AudioContext();
      const source = audioContext.createMediaStreamSource(stream);
      analyser = audioContext.createAnalyser();
      analyser.fftSize = 512;
      source.connect(analyser);

      const bufferLength = analyser.fftSize;
      dataArray = new Uint8Array(bufferLength);

      analyserRef.current = analyser;
      dataRef.current = dataArray;

      const calcVolume = () => {
        analyser.getByteTimeDomainData(dataArray);

        // calculate MSE
        let sum = 0;
        for (let i = 0; i < dataArray.length; i++) {
          const val = (dataArray[i] - 128) / 128;
          sum += val * val;
        }
        const rms = Math.sqrt(sum / dataArray.length);

        // smooth volume
        const smoothing = 0.8;
        smoothVolRef.current = smoothVolRef.current * smoothing + rms * (1 - smoothing);

        const threshold = 0.04;
        const adjusted =
          smoothVolRef.current < threshold ? 0 : smoothVolRef.current - threshold;

        setVolume(adjusted * 4); 
        setIsSpeaking(adjusted > 0);
        animationId = requestAnimationFrame(calcVolume);
      };
      calcVolume();


    }

    init();

    return () => cancelAnimationFrame(animationId);
    if(audioContext) audioContext.close();
    if(stream) {
      stream.getTracks().forEach((t) => t.stop());
    };
  }, []);

  const dotCount = 7;
  const dots = Array.from({length: dotCount});

  return (
    <div
      style={{
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        gap: "8px",
        height: "50px",
      }}
    >
      {dots.map((_, i) => {
        const phase = Math.sin(Date.now() / 150 + i * 0.8);
        const scaleY = 1 + volume * (2 + phase);
        return (
          <div
            key={i}
            style={{
              width: "8px",
              height: "8px",
              borderRadius: "50%",
              backgroundColor: isSpeaking ? "#3ba9ff" : "#ccc",
              transform: `scaleY(${scaleY})`,
              transition: "transform 0.1s ease-out, background-color 0.3s ease",
            }}
          ></div>
        );
      })}
    </div>
  );
}
