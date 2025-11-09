export default function LoadingOverlay({ text = "Analyzing… please wait" }) {
  return (
    <div className="overlay" role="alert" aria-busy="true" aria-live="assertive">
      <div className="overlay-card">
        <div className="spinner" aria-hidden="true" />
        <div className="overlay-text">{text}</div>
      </div>
    </div>
  );
}