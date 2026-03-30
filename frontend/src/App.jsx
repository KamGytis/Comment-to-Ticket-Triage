import { useEffect, useState } from "react";
import { getComments } from "./api";
import CommentForm from "./components/CommentForm";
import CommentList from "./components/CommentList";

export default function App() {
  const [comments, setComments] = useState([]);

  useEffect(() => {
    loadComments();
  }, []);

  const loadComments = async () => {
    try {
      const data = await getComments();
      setComments(data);
    } catch (err) {
      console.error("Failed to load comments:", err);
    }
  };

  const handleNewComment = (comment) => {
    setComments((prev) => [comment, ...prev]);
  };

  return (
    <div className="container">
      <h1>Comment → Ticket System</h1>
      <CommentForm onNewComment={handleNewComment} />
      <CommentList comments={comments} />
    </div>
  );
}