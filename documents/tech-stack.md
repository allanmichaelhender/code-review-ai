# Technology Stack

## Backend

| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| Runtime | Java | 21 | Application runtime |
| Framework | Spring Boot | 3.2.0 | Application framework |
| Database | PostgreSQL | 15 | Primary data storage |
| Cache | Redis | 7 | Analysis result caching |
| ORM | Hibernate | (via Spring Data JPA) | Database abstraction |
| Git Operations | JGit | 6.8.0 | Repository cloning |
| LLM Provider | OpenRouter API | - | AI-powered code analysis |
| LLM Model | NVIDIA Nemotron-3 Nano 30B | free tier | Code analysis model |
| Build Tool | Maven | 3.x | Dependency management |
| Scheduling | Spring Scheduling | - | Automated analysis jobs |

## Frontend

| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| Framework | React | 18 | UI framework |
| Language | TypeScript | 5.x | Type-safe JavaScript |
| Build Tool | Vite | 5.0 | Fast development server |
| Styling | TailwindCSS | 4 | Utility-first CSS |
| Components | shadcn/ui | - | Pre-built UI components |
| Icons | Lucide React | - | Icon library |
| Routing | React Router | 6.x | Client-side routing |

## Infrastructure

| Component | Technology | Purpose |
|-----------|-----------|---------|
| Containerization | Docker | Application packaging |
| Orchestration | Docker Compose | Multi-container management |
| Reverse Proxy | Nginx | Production routing |
| Database UI | pgAdmin 4 | Database management |
