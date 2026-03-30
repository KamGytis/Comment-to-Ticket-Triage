export default function CommentList({ comments }) {
  return (
    <div>
      <h2>Comments</h2>
      {comments.map(c => (
        <div key={c.id} style={{ border: "1px solid #ccc", margin: "10px", padding: "10px" }}>
          <p><strong>{c.text}</strong></p>
          <p>Source: {c.source}</p>

          {c.isTicket ? (
            <>
              <p>Category: {c.category}</p>
              <p>Priority: {c.priority}</p>
              <p>Summary: {c.summary}</p>
              <p>Status: Ticket created</p>
            </>
          ) : (
            <p>Status: Only a comment</p>
          )}
        </div>
      ))}
    </div>
  );
}