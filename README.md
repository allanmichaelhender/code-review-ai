# Repo Review

An AI-powered code review platform built with Spring Boot and React TypeScript that analyzes GitHub repositories and individual files using LLMs (Gemini, OpenRouter + NVIDIA Nemotron) to identify security vulnerabilities and code quality issues.

## Tech Stack

**Backend:**

- Spring Boot 3.2.0 (Java 21)
- PostgreSQL 15
- Redis 7
- JGit 6.8.0 for repository cloning
- Google Gemini API (gemini-3.1-flash-lite-preview)
- OpenRouter API with NVIDIA Nemotron-3 Nano 30B model (free tier)
- Spring Data JPA + Hibernate
- Spring Scheduling for automated weekly analysis

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
- pgAdmin 4 for database management (http://localhost:5050)

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
- `OPEN_ROUTER_API_KEY`: Your OpenRouter API key
- `GITHUB_API_TOKEN`: Your GitHub API token (for private repositories)
- `PGADMIN_DEFAULT_EMAIL`: Email for pgAdmin login (default: admin@codereview.local)
- `PGADMIN_DEFAULT_PASSWORD`: Password for pgAdmin login (default: admin123)

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
- pgAdmin: http://localhost:5050

### Production Deployment

The application is deployed with HTTPS using Let's Encrypt SSL certificates:

- **Domain**: repo-reviewer.ddnsfree.com
- **HTTP**: Redirects to HTTPS
- **HTTPS**: Port 443 with TLSv1.2/TLSv1.3
- **SSL Certificates**: Located at `/etc/letsencrypt/live/repo-reviewer.ddnsfree.com/`
- **ACME Challenge**: Handled at `/.well-known/acme-challenge/`

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
  - Best practices violations
  - Maintainability concerns
  - Repository analysis: Uses OpenRouter NVIDIA Nemotron-3 Nano 30B (MVP limit: 1 file per repository)
  - Single file analysis: Uses Gemini gemini-3.1-flash-lite-preview (analyze specific GitHub files)

- **Scheduled Daily Analysis**: Automatically analyzes a subset of repositories every weekday at midnight
  - Distributes repositories across 5 weekdays (Monday-Friday) to avoid API rate limits
  - Each repository assigned to a specific day based on ID modulo 5
  - Retry logic with exponential backoff for OpenRouter rate limits
  - DataLoader auto-updates project names and tech stacks on startup
  - Manual trigger endpoint: `POST /api/admin/trigger-analysis` (analyzes all repositories)
  - Comprehensive logging and error handling
  - Uses OpenRouter NVIDIA Nemotron-3 Nano 30B model

- **Pre-Analysed Repository Showcase**: View pre-cached analysis results for sample repositories
  - Project names displayed (e.g., "Guinea Pig Portfolio" instead of owner/name)
  - Tech stack badges showing frontend and backend frameworks

- **Single File Analysis**: Analyze individual GitHub files using Gemini (gemini-3.1-flash-lite-preview)
  - Enter GitHub file URL directly
  - Auto-detects language from file extension
  - Fetches file content via GitHub API
  - Instant analysis results displayed on landing page
- **Repository Analysis**: Analyze entire repositories (disabled in demo mode)

### UI Features

- Modern, responsive design with TailwindCSS 4
- Two-column landing page layout (5/7 left for analysis, 1/3 right for demo repositories)
- Single file analysis with Analyse button inside input box
- File/Repo mode toggle slider (Repo disabled in demo mode)
- Analysis results expand to fill left column after submission
- Analysis categories displayed without title at bottom of left column
- Demo Repositories column with health score visualization
- Mobile responsive: columns stack vertically, slider below input, 2-column grid for repos
- Vertically and horizontally centered content
- Uses 95% of viewport width on desktop
- shadcn/ui components for consistent styling
- Health score visualization with color-coded indicators
- Card-based layout for repository listing with project names and tech stacks
- Dark mode support
- British English spelling ("analysed" instead of "analyzed")

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
  - Using Hibernate ddl-auto: update for automatic schema sync
  - Redis caching for analysis results (24-hour TTL)
  - DataLoader component auto-updates project names and tech stacks
  - CodeRepository entity includes projectName and techStack fields

### Analysis Schema

- **AnalysisResult**: category, type, severity, explanation, confidence/impact/effort scores, CWE ID, OWASP category
- **Analysis**: category counts, health scores per category, analysis duration, tokens used, model version

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
- ✅ LLM API: Requires valid API keys (Gemini, OpenRouter)
- ✅ Smart repository cloning with file filtering
- ✅ OpenRouter API integration with NVIDIA Nemotron-3 Nano 30B model (free tier)
- ✅ Redis caching for analysis results
- ✅ Enhanced analysis schema with categories and health scores
- ✅ Seed data with pre-cached repository analysis (disabled for real analysis)
- ✅ Docker configuration for development and production
- ✅ TailwindCSS 4 and shadcn/ui components
- ✅ Explore page with card layout and health score visualization
- ✅ Landing page redesign with hero section and features
- ✅ Demo page improvements with loading states and error handling
- ✅ pgAdmin 4 integration for database management
- ✅ Fixed repository lookup to use URL instead of owner/name
- ✅ Frontend configured to use OpenRouter provider by default
- ✅ Reduced file analysis limit to 1 file to stay within 50 daily request limit
- ✅ Added projectName and techStack fields to CodeRepository entity
- ✅ Updated frontend to display project names and tech stack badges
- ✅ Removed manual analysis option from frontend (analysis via scheduled scripts only)
- ✅ Merged repository list into Home page, removed Explore route
- ✅ Renamed "analyzed" to "analysed" throughout UI (British English)
- ✅ Added retry logic with exponential backoff for OpenRouter rate limits
- ✅ Disabled React StrictMode to prevent duplicate API calls in development
- ✅ DataLoader auto-updates project names and tech stacks on startup
- ✅ Fixed frontend proxy timeout (5 minutes) for long-running analysis
- ✅ Scheduled daily analysis job (runs Monday-Friday at midnight)
- ✅ Repositories distributed across weekdays to avoid API rate limits
- ✅ Manual trigger endpoint for testing (analyzes all repositories)
- ✅ Added analysis categories bar to Landing page with 1-line explainers
- ✅ Reduced spacing on Landing page to fit on one desktop screen
- ✅ Changed repository card title to plain text with Code button (ExternalLink icon)
- ✅ Introduced Emerald accent color to site (titles, buttons, hover effects)
- ✅ Removed accent color hover from analysis category cards to avoid confusion
- ✅ Fixed vite.config.ts to use Docker service name instead of hardcoded IP
- ✅ Added fake analysis data seeding for Vantage Point and Hybrid Hour repositories
- ✅ Added deleteByAnalysisId and findByRepositoryId methods to repositories
- ✅ Seeding logic replaces analyses with zero issues or empty results with fake data
- ✅ Fixed Docker networking issues (frontend proxy using hardcoded IP)
- ✅ Updated Gemini model to gemini-3.1-flash-lite-preview
- ✅ Added single file analysis endpoint (POST /api/analyze-file)
- ✅ Added GitHub file URL input to landing page
- ✅ Redesigned landing page with animated gradient background and glassmorphism
- ✅ Changed app name to "Repo Review"
- ✅ Made landing page fit on one screen without scrolling (desktop)
- ✅ Disabled repository analysis in demo mode
- ✅ Removed repository list and categories from landing page

### In Progress

- ⏳ Testing real DeepSeek analysis on live repositories

### Planned

- 📋 Real-time analysis streaming (WebSocket)
- 📋 Authentication and authorization
- 📋 Analysis history and comparison
- 📋 Production deployment configuration
- 📋 Additional LLM providers (Claude, GPT-4)
- 📋 Export functionality (PDF reports)

## License

MIT
