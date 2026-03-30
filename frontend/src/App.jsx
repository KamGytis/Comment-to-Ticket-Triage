import { useEffect, useState } from "react";
import { getComments, getTickets } from "./api";
import CommentForm from "./components/CommentForm";
import CommentList from "./components/CommentList";
import TicketList from "./components/TicketList";

export default function App() {
  const [comments, setComments] = useState([]);
  const [tickets, setTickets] = useState([]);
  const [tab, setTab] = useState("comments"); // "comments" | "tickets"
  const [loadError, setLoadError] = useState(null);

  useEffect(() => {
    loadAll();
  }, []);

  const loadAll = async () => {
    try {
      const [c, t] = await Promise.all([getComments(), getTickets()]);
      setComments(c);
      setTickets(t);
    } catch (err) {
      console.error("Load failed:", err);
      setLoadError("Could not reach the backend. Is it running on port 8080?");
    }
  };

  const handleNewComment = async (comment) => {
   
    setComments((prev) => [comment, ...prev]);

  
    if (comment.convertedToTicket) {
      try {
        const updatedTickets = await getTickets();
        setTickets(updatedTickets);
        setTab("tickets"); 
      } catch (err) {
        console.error("Failed to reload tickets:", err);
      }
    }
  };

  const tabStyle = (name) => ({
    padding: "8px 20px",
    marginRight: 8,
    border: "none",
    borderRadius: "6px 6px 0 0",
    cursor: "pointer",
    fontWeight: tab === name ? "bold" : "normal",
    background: tab === name ? "#fff" : "#ddd",
    borderBottom: tab === name ? "2px solid #4CAF50" : "none",
  });

  return (
    <div className="container">
      <h1>💬 Comment → Ticket System</h1>
      {loadError && <p style={{ color: "red" }}>{loadError}</p>}

      <CommentForm onNewComment={handleNewComment} />

      <div style={{ marginTop: 24 }}>
        <button style={tabStyle("comments")} onClick={() => setTab("comments")}>
          Comments ({comments.length})
        </button>
        <button style={tabStyle("tickets")} onClick={() => setTab("tickets")}>
          Tickets ({tickets.length})
        </button>
      </div>

      <div style={{ borderTop: "2px solid #4CAF50", paddingTop: 16 }}>
        {tab === "comments" ? (
          <CommentList comments={comments} />
        ) : (
          <TicketList tickets={tickets} />
        )}
      </div>
    </div>
  );
}