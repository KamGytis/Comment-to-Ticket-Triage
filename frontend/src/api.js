const API_URL = "http://localhost:8080/comments";
 
function parseHfRaw(comment) {
  if (!comment.hfRaw) return comment;
  try {
    const raw =
      typeof comment.hfRaw === "string"
        ? JSON.parse(comment.hfRaw)
        : comment.hfRaw;
    const hfParsed = JSON.parse(raw.choices[0].message.content);
    return { ...comment, ...hfParsed };
  } catch (e) {
    console.error("HF parse error:", e);
    return comment;
  }
}
 
export async function getComments() {
  const res = await fetch(API_URL);
  if (!res.ok) throw new Error(`Failed to fetch comments: ${res.status}`);
  const data = await res.json();
  return data.map(parseHfRaw);
}
 
export async function createComment(data) {
  const res = await fetch(API_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  if (!res.ok) throw new Error(`Failed to create comment: ${res.status}`);
  const newComment = await res.json();
  return parseHfRaw(newComment);
}