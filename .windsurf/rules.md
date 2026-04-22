# AI Code Review Platform - Project Context

## Project Overview

AI-powered code review platform built with Spring Boot (Java 21) and React (TypeScript) that analyzes GitHub repositories using LLMs (Gemini, OpenRouter + NVIDIA Nemotron) to identify security vulnerabilities and code quality issues.

### Tech Stack

**Backend:**

- Java 21
- Spring Boot 3.2.0
- Spring Data JPA with Hibernate
- PostgreSQL database
- Redis for caching
- JGit for Git operations
- Lombok for code generation
- Maven for dependency management
- Hibernate ddl-auto: update for automatic schema sync
- OpenRouter API with NVIDIA Nemotron-3 Nano 30B model (free tier)
- Spring Scheduling for automated weekly analysis

### Frontend

- **Framework**: React 18 with TypeScript
- **Build Tool**: Vite 5.0
- **UI Library**: TailwindCSS 4 + shadcn/ui components
- **Routing**: React Router 6
- **Icons**: Lucide React

### Infrastructure

- **Containerization**: Docker + Docker Compose
- **Reverse Proxy**: Nginx
- **Development**: Hot reload with docker-compose.override.yml
- **Database Management**: pgAdmin 4 (http://localhost:5050)

## Project Structure

```
code-review-ai/
├── backend/
│   ├── src/main/java/com/codereview/
│   │   ├── model/           # JPA entities (CodeRepository, Analysis, AnalysisResult)
│   │   ├── repository/      # Spring Data JPA repositories
│   │   ├── service/         # Business logic (AnalysisService, GitService, LLMService)
│   │   │   └── llm/        # LLM provider implementations (Gemini, DeepSeek)
│   │   ├── controller/      # REST controllers
│   │   ├── config/         # Spring configuration (DataLoader, SecurityConfig)
│   │   └── CodeReviewApplication.java
│   ├── src/main/resources/
│   │   ├── application.yml # Spring configuration
│   │   ├── data.sql        # Seed data (fallback)
│   │   └── db/migration/   # Flyway migrations (disabled)
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/ui/   # shadcn/ui components
│   │   ├── lib/            # Utilities (utils.ts)
│   │   ├── pages/          # Page components (Landing, Demo, Explore)
│   │   ├── App.tsx
│   │   └── index.css       # TailwindCSS imports
│   ├── public/
│   ├── Dockerfile.dev
│   ├── tailwind.config.ts
│   ├── vite.config.ts
│   └── package.json
├── nginx/
│   ├── nginx.conf
│   └── Dockerfile
├── docs/
│   ├── architecture/      # Architecture documentation
│   ├── plans/             # Project plans
│   └── summaries/         # Meeting summaries
├── docker-compose.yml
├── docker-compose.override.yml
└── README.md
```

## Key Architecture Decisions

### Entity Naming

- **Important**: `Repository` entity renamed to `CodeRepository` to avoid naming conflicts with Spring's `@Repository` annotation
- Repository interface renamed to `CodeRepositoryRepository`
- All references updated in services, controllers, and repositories

### Database Schema

- Using BIGSERIAL for ID columns to match JPA Long type
- Using Hibernate ddl-auto: update for automatic schema sync
- Redis caching for analysis results (24-hour TTL)
- DataLoader component auto-updates project names and tech stacks on startup
- CodeRepository entity includes projectName and techStack fields

### Analysis Schema

- **AnalysisResult**: category, type, severity, explanation, confidence/impact/effort scores, CWE ID, OWASP category
- **Analysis**: category counts, health scores per category, analysis duration, tokens used, model version

### Git Operations (Smart Cloning)

- Shallow cloning (--depth 1) to save bandwidth
- Single branch cloning
- Aggressive file filtering to exclude non-code files
- Auto-cleanup of cloned repositories
- Binary file detection via null bytes and non-printable character analysis
- Max 1 file analyzed per repository to stay within 50 daily request limit

### File Filtering

The platform automatically excludes:

- **Model files**: .pt, .pth, .h5, .pkl, .onnx, .bin, .safetensors, .gguf, .ckpt
- **Video files**: .mp4, .avi, .mov, .mkv, .flv, .wmv, .webm
- **Audio files**: .mp3, .wav, .flac, .aac, .ogg, .m4a
- **Large files**: > 5MB (images > 1MB)
- **Archives**: .zip, .tar, .gz, .rar, .7z, .bz2
- **Binaries**: .exe, .dll, .so, .dylib, .app, .deb, .rpm
- **Data files**: .csv, .json, .xml, .yaml, .yml, .parquet, .feather
- **Database files**: .db, .sqlite, .mdb
- **Compiled files**: .class, .pyc, .pyo, .jar, .war, .ear
- **Common directories**: node_modules, .git, **pycache**, venv, env, dist, build, target, bin, obj, .next, .nuxt, out, .cache, models, data, datasets, checkpoints, logs, temp, tmp

### Frontend Configuration

- TailwindCSS 4 with Vite plugin (no PostCSS config needed)
- Path alias configured: `@` maps to `./src`
- Development server on port 8000
- Vite proxy to backend: `http://backend:8080` (Docker service name, not hardcoded IP)
- Proxy timeout: 5 minutes for long-running analysis requests
- React StrictMode disabled to prevent duplicate API calls in development
- British English spelling ("analysed" instead of "analyzed")
- Project names and tech stack badges displayed on repository cards
- Emerald accent color for titles, buttons, and hover effects
- Repository card titles use emerald-300 by default, emerald-200 on hover
- Tech stack badges use emerald on hover
- Repository cards have emerald border on hover

### Scheduled Analysis

- **Daily Job**: Automatically analyzes a subset of repositories every weekday at midnight
- **Cron Expression**: `0 0 0 ? * MON-FRI`
- **Distribution**: Repositories distributed across 5 weekdays (Monday-Friday) based on ID % 5
- **Purpose**: Avoids API rate limits by spreading analysis across days
- **Service**: `ScheduledAnalysisService` with `@Scheduled` annotation
- **Manual Trigger**: `POST /api/admin/trigger-analysis` (analyzes all repositories)
- **Error Handling**: Comprehensive logging and per-repository error tracking
- **Retry Logic**: Exponential backoff for OpenRouter rate limits
- **Model**: Uses OpenRouter NVIDIA Nemotron-3 Nano 30B model

### Analysis Categories

- **SECURITY**: SQL injection, XSS, hardcoded secrets, authentication issues, input validation
- **CODE_QUALITY**: Complexity, duplication, maintainability, code smells
- **PERFORMANCE**: Inefficient algorithms, resource leaks, performance bottlenecks
- **BEST_PRACTICES**: Language-specific best practices, design patterns
- **MAINTAINABILITY**: Documentation, naming conventions, code organization

### Analysis Severity Levels

- **critical**: Security vulnerabilities, data exposure
- **high**: Major issues that should be fixed
- **medium**: Moderate issues
- **low**: Minor issues
- **info**: Informational notes

### Analysis Health Scores

- **overall_health_score**: 0.0 to 1.0, overall repository health
- **security_health_score**: 0.0 to 1.0, security-specific health
- **code_quality_health_score**: 0.0 to 1.0, code quality health
- **performance_health_score**: 0.0 to 1.0, performance health

### Caching Strategy

- **Redis** for caching analysis results
- **Cache key**: `analysis:{repoUrl}:{commitHash}`
- **TTL**: 24 hours
- **Cache invalidation**: On new commits

## Environment Variables

Required:

- `GEMINI_API_KEY`: Google Gemini API key
- `OPEN_ROUTER_API_KEY`: OpenRouter API key
- `GITHUB_API_TOKEN`: GitHub personal access token (for private repositories)
- `PGADMIN_DEFAULT_EMAIL`: Email for pgAdmin login
- `PGADMIN_DEFAULT_PASSWORD`: Password for pgAdmin login

Database (defaults in docker-compose.yml):

- `DATABASE_URL`: jdbc:postgresql://postgres:5432/codereview
- `DATABASE_USERNAME`: codereview
- `DATABASE_PASSWORD`: codereview

Redis:

- `REDIS_HOST`: localhost
- `REDIS_PORT`: 6379

## Development Workflow

### Running the Application

**Development mode (hot reload):**

```bash
docker compose -f docker-compose.yml -f docker-compose.override.yml up -d
```

**Production mode:**

```bash
docker compose up -d
```

**Local development:**

```bash
# Backend
cd backend
mvn spring-boot:run

# Frontend
cd frontend
npm install
npm run dev
```

### URLs

- Frontend (dev): http://localhost:8000
- Frontend (prod): http://localhost
- Backend API: http://localhost:8080/api
- Nginx: http://localhost
- pgAdmin: http://localhost:5050

## Code Conventions

### Backend (Java)

- Use Spring Boot conventions
- Lombok for reducing boilerplate
- Service layer for business logic
- Repository layer for data access
- Controller layer for REST endpoints
- Use `@Transactional` for database operations
- Follow JPA entity naming conventions

### Frontend (TypeScript/React)

- Use functional components with hooks
- Use TypeScript for type safety
- Follow React best practices
- Use TailwindCSS for styling
- Use shadcn/ui components for consistency
- Path alias `@` for imports from `./src`

### Git Conventions

- Use clear, descriptive commit messages
- Branch naming: `feature/`, `bugfix/`, `hotfix/`
- Never commit `.env` file with real API keys
- Use `.gitignore` to exclude sensitive files

## Common Issues

### Docker Build Errors

- If backend fails to build, check for Java compilation errors
- If frontend fails, check npm dependencies and TypeScript errors
- Use `docker compose up -d --build <service>` to rebuild specific services

### Database Issues

- PostgreSQL schema uses BIGSERIAL, ensure JPA uses Long type
- Flyway is disabled, Hibernate manages schema with ddl-auto: update
- Seed data loaded via DataLoader component

### Frontend Issues

- TailwindCSS 4 uses Vite plugin, no PostCSS config needed
- Path alias `@` must be configured in both vite.config.ts and tsconfig.json
- Vite dev server runs on port 8000 in Docker

## API Endpoints

### Repositories

- `GET /api/repositories` - List all repositories
- `POST /api/repositories` - Create a new repository

### Analysis

- `POST /api/analyze?repo=<url>` - Analyze a repository
- `GET /api/analysis/{id}` - Get analysis by ID
- `GET /api/analysis/{id}/results` - Get analysis results
- `GET /api/analysis/by-repo?repo=<url>` - Get latest analysis by repository URL

### Admin

- `POST /api/admin/trigger-analysis` - Manually trigger analysis (analyzes all repositories)

## Current Status

### Completed

- ✅ Project structure and basic setup
- ✅ Spring Boot backend with JPA, PostgreSQL, Redis
- ✅ React TypeScript frontend with Vite
- ✅ LLM provider abstraction (Gemini, OpenRouter)
- ✅ Smart repository cloning with file filtering
- ✅ Basic analysis service (security + code quality)
- ✅ Seed data with pre-cached repository analysis (disabled for real analysis)
- ✅ Docker configuration for development and production
- ✅ TailwindCSS 4 and shadcn/ui components
- ✅ Explore page with card layout and health score visualization
- ✅ Landing page redesign with hero section and features
- ✅ Demo page improvements with loading states and error handling
- ✅ pgAdmin 4 integration for database management
- ✅ Fixed repository lookup to use URL instead of owner/name
- ✅ Frontend configured to use OpenRouter provider by default
- ✅ OpenRouter API integration with NVIDIA Nemotron-3 Nano 30B model (free tier)
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

### In Progress

- ⏳ Testing real OpenRouter analysis on live repositories

### Planned

- 📋 Real-time analysis streaming (WebSocket)
- 📋 Additional analysis types (performance, dependency scanning)
- 📋 Authentication and authorization
- 📋 Analysis history and comparison
- 📋 Production deployment configuration
- 📋 Additional LLM providers (Claude, GPT-4, DeepSeek fallback)
- 📋 Export functionality (PDF reports)

## Documentation

- **README.md**: Project overview and getting started
- **.windsurf/rules.md**: This file - project context for AI assistance
- **.windsurf/project-plan.md**: Current status, next steps, and roadmap
