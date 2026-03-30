function formatDate(dateStr) {
  if (!dateStr) return "";
  return new Date(dateStr).toLocaleString();
}

export default function CommentList({ comments }) {
  if (comments.length === 0) {
    return (
      <div>
        <h2>Comments</h2>
        <p style={{ color: "#999" }}>No comments yet.</p>
      </div>
    );
  }

  return (
    <div>
      <h2>Comments</h2>
      {comments.map((c) => (
        <div key={c.id} className="comment">
          <h3>{c.text}</h3>
          <p>Source: <strong>{c.source}</strong></p>
          <p>
            Status:{" "}
            {c.convertedToTicket ? (
              <span style={{ color: "#e67e22" }}>🎫 Ticket created</span>
            ) : (
              <span style={{ color: "#27ae60" }}>💬 Comment only</span>
            )}
          </p>
          <p style={{ color: "#999", fontSize: 12 }}>{formatDate(c.createdAt)}</p>
        </div>
      ))}
    </div>
  );
}