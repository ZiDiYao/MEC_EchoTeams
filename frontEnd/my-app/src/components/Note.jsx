// src/components/Note.jsx
export default function Note({ value, onChange, placeholder }) {
  return (
    <textarea
      className="big-input"
      value={value}
      onChange={(e) => onChange(e.target.value)}
      placeholder={placeholder}
    />
  );
}