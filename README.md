# AI Code Review Platform

An AI-powered code review platform built with Spring Boot and React TypeScript that analyzes GitHub repositories using LLMs (Gemini, DeepSeek) to identify security vulnerabilities and code quality issues.

## Tech Stack

**Backend:**

- Spring Boot 3.2.0 (Java 21)
- PostgreSQL 15
- Redis 7
- JGit 6.8.0 for repository cloning
- Google Gemini API
- DeepSeek API
- Spring Data JPA + Hibernate

**Frontend:**

- React 18 + TypeScript
- Vite 5.0
- React Router 6
- TailwindCSS 4
- shadcn/ui components
- Lucide React icons

**Infrastructure:**

- Docker Compose
- Nginx reverse proxy
- Docker multi-stage builds

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
- `GITHUB_API_TOKEN`: Your GitHub API token (for private repositories)

### Running with Docker

**Production mode:**

```bash
docker compose up -d
```

**Development mode (with hot reload):**

```bash
docker compose -f docker-compose.yml -f docker-compose.override.yml up -d
```

The application will be available at:

- Frontend: http://localhost:8000 (dev mode) or http://localhost (production)
- Backend API: http://localhost:8080/api
- Nginx: http://localhost (production)

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
│   ├── src/main/java/    # Java source code
│   ├── src/main/resources/  # Configuration files
│   └── pom.xml          # Maven dependencies
├── frontend/             # React TypeScript application
│   ├── src/              # Source code
│   ├── public/           # Static assets
│   └── package.json     # NPM dependencies
├── nginx/                # Nginx configuration
├── docs/                 # Documentation
│   ├── architecture/     # Architecture documentation
│   ├── plans/            # Project plans
│   └── summaries/       # Meeting summaries
├── docker-compose.yml    # Docker orchestration
├── docker-compose.override.yml  # Development override
└── README.md
```

## Features

### Core Functionality

- **Smart Repository Cloning**: Automatically clones GitHub repositories with intelligent file filtering
  - Excludes heavy files (models, videos, large binaries > 5MB)
  - Excludes common directories (node_modules, .git, **pycache**, etc.)
  - Shallow clone optimization (--depth 1)
  - Binary file detection
  - Auto-cleanup after analysis

- **AI-Powered Analysis**: Uses LLMs to analyze code for:
  - Security vulnerabilities (hardcoded secrets, SQL injection, XSS, etc.)
  - Code quality issues (complexity, duplication, best practices)
  - Performance issues
  - Dependency vulnerabilities

- **Pre-Analyzed Repository Showcase**: View pre-cached analysis results for sample repositories

- **Real Analysis**: Analyze any public GitHub repository with LLM-powered insights

### UI Features

- Modern, responsive design with TailwindCSS 4
- shadcn/ui components for consistent styling
- Health score visualization with color-coded indicators
- Card-based layout for repository listing
- Dark mode support

## Smart File Filtering

The platform automatically excludes the following files during analysis:

**Model Files:** .pt, .pth, .h5, .pkl, .onnx, .bin, .safetensors, .gguf, .ckpt

**Media Files:** .mp4, .avi, .mov, .mkv, .flv, .wmv, .webm, .mp3, .wav, .flac, .aac, .ogg, .m4a

**Large Files:** > 5MB (images > 1MB)

**Archives & Binaries:** .zip, .tar, .gz, .rar, .7z, .bz2, .exe, .dll, .so, .dylib

**Data Files:** .csv, .json, .xml, .yaml, .yml, .parquet, .feather, .db, .sqlite, .mdb

**Compiled Files:** .class, .pyc, .pyo, .jar, .war, .ear

**Common Directories:** node_modules, .git, **pycache**, venv, env, dist, build, target, bin, obj, .next, .nuxt, out, .cache, models, data, datasets, checkpoints, logs, temp, tmp

## Architecture Decisions

### Entity Naming

- Renamed `Repository` to `CodeRepository` to avoid naming conflicts with Spring's `@Repository` annotation

### Database Schema

- Using BIGSERIAL for ID columns to match JPA Long type
- Disabled Flyway to avoid migration checksum issues
- Using Hibernate ddl-auto: update for schema management
- DataLoader component for programmatic seed data

### Git Operations

- Shallow cloning (--depth 1) to save bandwidth
- Single branch cloning
- Aggressive file filtering to exclude non-code files
- Auto-cleanup of cloned repositories
- Binary file detection via null bytes and non-printable character analysis

## Documentation

- **Project Plan**: See `docs/plans/ai-code-review-platform.md` for detailed project status and next steps
- **Architecture**: See `docs/architecture/` for technical architecture documentation

## Current Status

### Completed

- ✅ Project structure and basic setup
- ✅ Spring Boot backend with JPA, PostgreSQL, Redis
- ✅ React TypeScript frontend with Vite
- ✅ LLM provider abstraction (Gemini, DeepSeek)
- ✅ Smart repository cloning with file filtering
- ✅ Basic analysis service (security + code quality)
- ✅ Seed data with pre-cached repository analysis
- ✅ Docker configuration for development and production
- ✅ TailwindCSS 4 and shadcn/ui components
- ✅ Explore page with card layout and health score visualization

### In Progress

- ⏳ Landing page redesign with hero section
- ⏳ Demo page improvements with better loading states
- ⏳ Responsive design enhancements

### Planned

- 📋 Real-time analysis streaming (WebSocket)
- 📋 Additional analysis types (performance, dependency scanning)
- 📋 Authentication and authorization
- 📋 Analysis history and comparison
- 📋 Production deployment configuration

## License

MIT
