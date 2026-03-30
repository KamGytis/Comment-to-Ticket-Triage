# PulseDesk — Comment-to-Ticket Triage

An AI-powered support ticket generator built for IBM's internship technical challenge.
PulseDesk collects user comments and automatically determines whether they should become
support tickets using the Hugging Face Inference API.

## How It Works

1. A user submits a comment via the API or UI
2. The comment is analyzed by **Meta Llama 3.1 8B** via Hugging Face's Inference Router
3. If the comment describes a bug, complaint, or request, a structured ticket is generated
4. The ticket is stored with a title, category, priority, and summary

## Tech Stack

- **Backend**: Java 21, Spring Boot 3.3.5, Spring Data JPA
- **Database**: H2 (in-memory)
- **AI**: Hugging Face Inference API — `meta-llama/llama-3.1-8b-instruct`
- **Frontend**: React 18, Vite 5
- **Deployment**: Railway

## Live Demo

- Frontend: https://practical-achievement-production.up.railway.app
- Backend API: https://comment-to-ticket-triage-production.up.railway.app

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/comments` | Submit a comment for analysis |
| GET | `/comments` | View all submitted comments |
| GET | `/tickets` | View all generated tickets |
| GET | `/tickets/{id}` | View a single ticket by ID |

### Example Request
```bash
curl -X POST https://comment-to-ticket-triage-production.up.railway.app/comments \
  -H "Content-Type: application/json" \
  -d '{"text": "The app crashes every time I try to login", "source": "web"}'
```

### Example Response
```json
{
  "id": 1,
  "text": "The app crashes every time I try to login",
  "source": "web",
  "createdAt": "2026-03-30T18:49:55",
  "convertedToTicket": true
}
```

## Local Setup

### Prerequisites
- Java 21
- Maven

### Steps

1. Clone the repository:
```bash
git clone https://github.com/KamGytis/Comment-to-Ticket-Triage.git
cd Comment-to-Ticket-Triage
```

2. Set your Hugging Face API token as an environment variable:
```bash
# Windows PowerShell
$env:HF_TOKEN = "hf_your_token_here"

# Mac/Linux
export HF_TOKEN=hf_your_token_here
```

3. Run the backend:
```bash
./mvnw spring-boot:run
```

4. Run the frontend:
```bash
cd frontend
npm install
npm run dev
```

5. Open http://localhost:5173 in your browser

## Running with Docker

### Prerequisites
- Docker Desktop installed and running

### Steps

1. Clone the repository:
```bash
git clone https://github.com/KamGytis/Comment-to-Ticket-Triage.git
cd Comment-to-Ticket-Triage
```

2. Build and run the backend:
```bash
docker build -t pulsedesk-backend .
docker run -p 8080:8080 -e HF_TOKEN=hf_your_token_here pulsedesk-backend
```

3. Build and run the frontend:
```bash
docker build -f Dockerfile.frontend -t pulsedesk-frontend .
docker run -p 80:80 -e VITE_API_URL=http://localhost:8080 pulsedesk-frontend
```

4. Open http://localhost in your browser

### Running both with Docker Compose

Create a `docker-compose.yml` in the root (if not already present):
```yaml
version: '3.8'
services:
  backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      - HF_TOKEN=hf_your_token_here

  frontend:
    build:
      context: .
      dockerfile: Dockerfile.frontend
      args:
        - VITE_API_URL=http://localhost:8080
    ports:
      - "80:80"
    depends_on:
      - backend
```

Then run:
```bash
docker-compose up --build
```

## Notes on AI Model

The originally suggested models (`mistralai/Mistral-7B-Instruct`, `google/flan-t5-base`)
returned `410 Gone` on the free Hugging Face Inference API tier as of March 2026.
This project uses `meta-llama/llama-3.1-8b-instruct` via Hugging Face's new
Inference Router (`router.huggingface.co`), which is still a Hugging Face model
and API as specified in the requirements.