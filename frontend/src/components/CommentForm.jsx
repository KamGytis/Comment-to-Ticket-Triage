import { useState } from "react";

// Mock HF call function
async function analyzeCommentHF(text) {
  // Replace this with your real Hugging Face endpoint
  const res = await fetch("YOUR_HF_API_ENDPOINT", {
    method: "POST",
    headers: {
      "Authorization": "Bearer YOUR_HF_KEY",
      "Content-Type": "application/json"
    },
    body: JSON.stringify({ inputs: text })
  });

  const data = await res.json();
  
  // HF response is inside choices[0].message.content
  return JSON.parse(data.choices[0].message.content);
}

export default function CommentForm({ onNewComment }) {
  const [text, setText] = useState("");
  const [source, setSource] = useState("web");

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!text) return;

    // Analyze comment with HF
    const hfResult = await analyzeCommentHF(text);

    const newComment = {
      id: Date.now(),
      text,
      source,
      ...hfResult
    };

    setText("");
    onNewComment(newComment);
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>Add Comment</h2>
      <input
        type="text"
        placeholder="Write comment..."
        value={text}
        onChange={e => setText(e.target.value)}
      />
      <select value={source} onChange={e => setSource(e.target.value)}>
        <option value="web">Web</option>
        <option value="email">Email</option>
      </select>
      <button type="submit">Submit</button>
    </form>
  );
}