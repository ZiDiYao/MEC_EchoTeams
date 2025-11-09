// src/components/Transcript.jsx
export default function Transcript({ text, onChange, placeholder }) {
  return (
    <textarea
      className="big-input"
      value={text}
      onChange={(e) => onChange(e.target.value)}
      placeholder={placeholder}
    />
  );
}
