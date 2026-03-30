function formatDate(dateStr) {
  if (!dateStr) return "";
  return new Date(dateStr).toLocaleString();
}

const PRIORITY_COLORS = {
  high: "#e74c3c",
  medium: "#e67e22",
  low: "#27ae60",
};

const CATEGORY_ICONS = {
  bug: "🐛",
  feature: "✨",
  billing: "💳",
  account: "👤",
  other: "📋",
};

export default function TicketList({ tickets }) {
  if (tickets.length === 0) {
    return (
      <div>
        <h2>Tickets</h2>
        <p style={{ color: "#999" }}>No tickets yet. Submit a comment that describes an issue!</p>
      </div>
    );
  }

  return (
    <div>
      <h2>Tickets ({tickets.length})</h2>
      {tickets.map((t) => (
        <div key={t.id} className="comment ticket">
          <h3>
            {CATEGORY_ICONS[t.category] ?? "📋"} #{t.id} — {t.title}
          </h3>
          <p>
            <strong>Category:</strong> {t.category} &nbsp;|&nbsp;
            <strong>Priority:</strong>{" "}
            <span style={{ color: PRIORITY_COLORS[t.priority] ?? "#333", fontWeight: "bold" }}>
              {t.priority?.toUpperCase()}
            </span>
          </p>
          <p><strong>Summary:</strong> {t.summary}</p>
          <p style={{ color: "#999", fontSize: 12 }}>
            From comment ID: {t.commentId} &nbsp;·&nbsp; {formatDate(t.createdAt)}
          </p>
        </div>
      ))}
    </div>
  );
}