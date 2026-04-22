# AI Code Review Platform - Project Context

## Project Overview

AI-powered code review platform built with Spring Boot (Java 21) and React (TypeScript) that analyzes GitHub repositories using LLMs (Gemini, DeepSeek) to identify security vulnerabilities and code quality issues.

## Tech Stack

### Backend

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 21
- **Database**: PostgreSQL 15
- **Cache**: Redis 7
- **ORM**: Spring Data JPA + Hibernate
- **Git Operations**: JGit 6.8.0
- **LLM Integration**: Custom abstraction for Gemini and DeepSeek APIs

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
- Disabled Flyway to avoid migration checksum issues
- Using Hibernate ddl-auto: update for schema management
- DataLoader component for programmatic seed data

### Git Operations (Smart Cloning)

- Shallow cloning (--depth 1) to save bandwidth
- Single branch cloning
- Aggressive file filtering to exclude non-code files
- Auto-cleanup of cloned repositories
- Binary file detection via null bytes and non-printable character analysis
- Max 50 files analyzed per repository for MVP

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
- Vite proxy to backend: `http://backend:8080`

## Environment Variables

Required:

- `GEMINI_API_KEY`: Google Gemini API key
- `DEEPSEEK_API_KEY`: DeepSeek API key
- `GITHUB_API_TOKEN`: GitHub personal access token (for private repositories)

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

- ⏳ Define analysis output structure and data model
- ⏳ Implement DeepSeek API calls for code analysis
- ⏳ Implement analysis result caching in Redis
- ⏳ Create seed data with real DeepSeek analysis results

### Planned

- 📋 Landing page redesign with hero section
- 📋 Demo page improvements with better loading states
- 📋 Real-time analysis streaming (WebSocket)
- 📋 Additional analysis types (performance, dependency scanning)
- 📋 Authentication and authorization
- 📋 Analysis history and comparison
- 📋 Production deployment configuration

## Documentation

- **README.md**: Project overview and getting started
- **.windsurf/rules.md**: This file - project context for AI assistance
- **.windsurf/project-plan.md**: Current status, next steps, and roadmap
