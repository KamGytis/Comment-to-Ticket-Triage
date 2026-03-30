import { useState } from "react";
import { createComment } from "../api";

export default function CommentForm({ onNewComment }) {
  const [text, setText] = useState("");
  const [source, setSource] = useState("web");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!text.trim()) return;

    setLoading(true);
    setError(null);

    try {
      const newComment = await createComment(text, source);
      setText("");
      onNewComment(newComment);
    } catch (err) {
      console.error("Submit failed:", err);
      setError("Failed to submit. Is the backend running?");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>Add Comment</h2>
      <input
        type="text"
        placeholder="Write your comment..."
        value={text}
        onChange={(e) => setText(e.target.value)}
        disabled={loading}
      />
      <select value={source} onChange={(e) => setSource(e.target.value)} disabled={loading}>
        <option value="web">Web</option>
        <option value="email">Email</option>
        <option value="chat">Chat</option>
        <option value="app_review">App Review</option>
      </select>
      <button type="submit" disabled={loading || !text.trim()}>
        {loading ? "Analyzing..." : "Submit"}
      </button>
      {error && <p style={{ color: "red", marginTop: 8 }}>{error}</p>}
    </form>
  );
}