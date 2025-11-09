export default function Sidebar({ items, activeId, onSelect, onNew }) {
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