# AI Code Review Platform

An AI-powered code review platform built with Spring Boot and React TypeScript.

## Tech Stack

**Backend:**
- Spring Boot 3.x
- PostgreSQL 15
- Redis 7
- JGit
- Google Gemini API
- DeepSeek API

**Frontend:**
- React 18 + TypeScript
- Vite
- React Router

**Infrastructure:**
- Docker Compose
- Nginx reverse proxy

## Getting Started

### Prerequisites

- Docker and Docker Compose
- Java 21 (for local development)
- Node.js 20 (for local development)

### Environment Variables

Create a `.env` file in the root directory:

```bash
cp .env.example .env
```

Then add your API keys:
- `GEMINI_API_KEY`: Your Google Gemini API key
- `DEEPSEEK_API_KEY`: Your DeepSeek API key
- `GITHUB_API_TOKEN`: Your GitHub API token (optional)

### Running with Docker

```bash
docker-compose up -d
```

The application will be available at:
- Frontend: http://localhost
- Backend API: http://localhost/api
- Nginx: http://localhost

### Local Development

**Backend:**
```bash
cd backend
mvn spring-boot:run
```

**Frontend:**
```bash
cd frontend
npm install
npm run dev
```

## Project Structure

```
code-review-ai/
├── backend/              # Spring Boot application
├── frontend/             # React TypeScript application
├── nginx/                # Nginx configuration
├── docs/                 # Documentation
├── docker-compose.yml    # Docker orchestration
└── README.md
```

## Features

- Public playground for analyzing GitHub repositories
- Pre-analyzed repository showcase
- AI-powered security and code quality analysis
- Real-time analysis streaming (via WebSocket)
- Docker-based deployment

## License

MIT
