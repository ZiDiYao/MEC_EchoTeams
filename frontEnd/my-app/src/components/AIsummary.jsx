// src/components/Alsummary.jsx
export default function Alsummary({ text }) {
  return (
    <div className="summary-box">
      {text ? text : "Summary will appear here after Submit."}
    </div>
  );
}