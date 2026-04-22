# AI Code Review Platform - Project Plan

## Project Overview
AI-powered code review platform that analyzes GitHub repositories using LLMs (Gemini, DeepSeek) to identify security vulnerabilities and code quality issues.

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
- **UI Library**: TailwindCSS + shadcn/ui components
- **Routing**: React Router 6
- **Icons**: Lucide React

### Infrastructure
- **Containerization**: Docker + Docker Compose
- **Reverse Proxy**: Nginx
- **Development**: Hot reload with docker-compose.override.yml

## Completed Work

### Phase 1: Foundation (Completed)
- [x] Project structure setup
- [x] Spring Boot backend initialization
- [x] React TypeScript frontend with Vite
- [x] PostgreSQL schema and migrations
- [x] Docker configuration (docker-compose, Dockerfiles)
- [x] Basic backend entities and repositories
- [x] LLM provider abstraction (Gemini, DeepSeek)
- [x] Basic analysis service (security + code quality)
- [x] Frontend pages (Landing, Demo, Explore)
- [x] Frontend-backend API connection
- [x] Docker build fixes (naming conflicts, schema types)
- [x] Docker compose override for local development
- [x] Seed data with pre-cached repository analysis
- [x] End-to-end testing

### Phase 2: Smart Repository Cloning (Completed)
- [x] JGit-based repository cloning
- [x] File filtering to exclude heavy files:
  - Model files (.pt, .pth, .h5, .pkl, .onnx, .bin, .safetensors, .gguf, .ckpt)
  - Video files (.mp4, .avi, .mov, .mkv, .flv, .wmv, .webm)
  - Audio files (.mp3, .wav, .flac, .aac, .ogg, .m4a)
  - Large files (> 5MB)
  - Large images (> 1MB)
  - Archives (.zip, .tar, .gz, .rar, .7z, .bz2)
  - Binaries (.exe, .dll, .so, .dylib, .app, .deb, .rpm)
  - Data files (.csv, .json, .xml, .yaml, .yml, .parquet, .feather)
  - Database files (.db, .sqlite, .mdb)
  - Compiled files (.class, .pyc, .pyo, .jar, .war, .ear)
  - PDFs and office docs
  - Common directories (node_modules, .git, __pycache__, venv, dist, build, target, etc.)
- [x] Shallow clone optimization (--depth 1)
- [x] Binary file detection
- [x] Auto-cleanup after analysis
- [x] Real file analysis (max 50 files for MVP)
- [x] Language detection by file extension
- [x] Actual commit hash tracking

### Phase 3: UI Improvements (In Progress)
- [x] TailwindCSS setup
- [x] shadcn/ui components (Button, Card, Badge)
- [x] Path alias configuration (@/ imports)
- [x] Explore page redesign with cards
- [x] Health score visualization
- [ ] Landing page redesign
- [ ] Demo page improvements
- [ ] Responsive design enhancements

## Current Status

### Backend
- ✅ Entity models: CodeRepository, Analysis, AnalysisResult
- ✅ Repository layer with JPA
- ✅ AnalysisService with JGit integration
- ✅ LLM provider abstraction (Gemini, DeepSeek)
- ✅ Smart file filtering in GitService
- ✅ DataLoader for seed data
- ✅ REST API endpoints
- ✅ CORS configuration
- ✅ Database schema (BIGSERIAL IDs)
- ✅ Flyway disabled (using Hibernate ddl-auto: update)

### Frontend
- ✅ React 18 with TypeScript
- ✅ Vite build system
- ✅ React Router for navigation
- ✅ TailwindCSS configured
- ✅ shadcn/ui components added
- ✅ Path alias (@/ imports)
- ✅ Explore page with card layout
- ✅ Health score visualization
- ⏳ Landing page (needs redesign)
- ⏳ Demo page (needs improvements)
- ⏳ Responsive design

### Infrastructure
- ✅ Docker multi-stage builds
- ✅ Docker Compose orchestration
- ✅ Development override with hot reload
- ✅ PostgreSQL data persistence
- ✅ Redis for caching
- ✅ Nginx reverse proxy
- ✅ Environment variable configuration
- ✅ .gitignore for sensitive files

## Next Steps

### Priority 1: Complete UI Improvements
1. **Landing Page Redesign**
   - Add hero section with value proposition
   - Feature highlights
   - Call-to-action buttons
   - Responsive layout

2. **Demo Page Enhancements**
   - Better loading states
   - Error handling UI
   - Analysis result filtering
   - Export functionality

3. **Responsive Design**
   - Mobile-friendly layouts
   - Touch-friendly interactions
   - Responsive grid systems

### Priority 2: Enhanced Analysis Features
1. **Additional Analysis Types**
   - Performance analysis
   - Security scanning integration
   - Code complexity metrics
   - Dependency vulnerability checks

2. **Real-time Analysis**
   - WebSocket streaming
   - Progress indicators
   - Cancellation support
   - Background processing

### Priority 3: Production Readiness
1. **Authentication & Authorization**
   - GitHub OAuth integration
   - User management
   - Analysis history
   - API rate limiting

2. **Monitoring & Logging**
   - Application metrics
   - Error tracking (Sentry)
   - Performance monitoring
   - Log aggregation

3. **Deployment**
   - Production Docker configuration
   - CI/CD pipeline
   - Environment-specific configs
   - Health checks

## Architecture Decisions

### Entity Naming
- Renamed `Repository` to `CodeRepository` to avoid naming conflicts with Spring's `@Repository` annotation
- Updated all references in services, controllers, and repositories

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

### Development Workflow
- docker-compose.override.yml for local development
- Hot reload on frontend (port 8000)
- Vite proxy to backend service (backend:8080)
- Volume mounts for live code editing

## Known Issues & Limitations

### Current Limitations
1. **Analysis Limit**: Max 50 files analyzed per repository for MVP
2. **LLM API**: Requires valid API keys (Gemini, DeepSeek)
3. **GitHub Token**: Required for private repositories
4. **Memory Usage**: Large repositories may exceed memory limits
5. **Analysis Speed**: Dependent on LLM API response times

### Future Improvements
1. Implement pagination for file analysis
2. Add caching for repeated analyses
3. Implement queue system for analysis jobs
4. Add support for GitLab and Bitbucket
5. Implement incremental analysis (only changed files)

## Environment Variables

Required environment variables:
- `GEMINI_API_KEY`: Google Gemini API key
- `DEEPSEEK_API_KEY`: DeepSeek API key
- `GITHUB_API_TOKEN`: GitHub personal access token

Database configuration:
- `DATABASE_URL`: PostgreSQL connection string
- `DATABASE_USERNAME`: Database username
- `DATABASE_PASSWORD`: Database password

Redis configuration:
- `REDIS_HOST`: Redis server host
- `REDIS_PORT`: Redis server port

## Git Repository
- **URL**: https://github.com/allanmichaelhender/code-review-ai.git
- **Branch**: master
- **Status**: Active development

## Documentation
- Architecture docs: `docs/architecture/`
- Planning docs: `docs/plans/`
- Summaries: `docs/summaries/`
