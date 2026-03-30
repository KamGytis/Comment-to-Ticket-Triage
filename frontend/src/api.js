const BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";
 
// ── Comments ──────────────────────────────────────────────
 
export async function getComments() {
  const res = await fetch(`${BASE_URL}/comments`);
  if (!res.ok) throw new Error(`GET /comments failed: ${res.status}`);
  return res.json();
}
 
export async function createComment(text, source) {
  const res = await fetch(`${BASE_URL}/comments`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ text, source }),
  });
  if (!res.ok) throw new Error(`POST /comments failed: ${res.status}`);
  return res.json();
}
 
// ── Tickets ───────────────────────────────────────────────
 
export async function getTickets() {
  const res = await fetch(`${BASE_URL}/tickets`);
  if (!res.ok) throw new Error(`GET /tickets failed: ${res.status}`);
  return res.json();
}
 
export async function getTicketById(id) {
  const res = await fetch(`${BASE_URL}/tickets/${id}`);
  if (!res.ok) throw new Error(`GET /tickets/${id} failed: ${res.status}`);
  return res.json();
}