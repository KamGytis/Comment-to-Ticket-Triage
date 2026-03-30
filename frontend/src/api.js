const API_URL = "http://localhost:8080/comments";

export async function getComments() {
  const res = await fetch(API_URL);
  const data = await res.json();

  
  return data.map(comment => {
    if (comment.hfRaw) { 
      try {
        const hfParsed = JSON.parse(comment.hfRaw.choices[0].message.content);
        return { ...comment, ...hfParsed };
      } catch (e) {
        console.error("HF parse error:", e);
        return comment;
      }
    }
    return comment;
  });
}

export async function createComment(data) {
  const res = await fetch(API_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });

  const newComment = await res.json();

 
  if (newComment.hfRaw) {
    try {
      const hfParsed = JSON.parse(newComment.hfRaw.choices[0].message.content);
      return { ...newComment, ...hfParsed };
    } catch (e) {
      console.error("HF parse error:", e);
    }
  }

  return newComment;
}