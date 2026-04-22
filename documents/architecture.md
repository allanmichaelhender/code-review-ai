# Architecture Overview

## System Architecture

```
┌─────────────────┐
│   Nginx (80/443) │
└────────┬────────┘
         │
         ├────────────────┬────────────────┐
         │                │                │
┌────────▼────────┐ ┌────▼──────────┐ ┌──▼──────────┐
│  React Frontend │ │  Spring Boot  │ │  PostgreSQL  │
│   (Vite/8000)   │ │  Backend (8080)│ │   (5432)     │
└────────┬────────┘ └────┬──────────┘ └─────────────┘
         │                │
         │                │
         │         ┌──────▼──────┐
         │         │    Redis     │
         │         │    (6379)    │
         │         └─────────────┘
         │
         │    ┌────▼──────────────┐
         │    │  OpenRouter API   │
         │    │  (NVIDIA Nemotron)│
         │    └───────────────────┘
```

## Backend Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Spring Boot Application               │
├─────────────────────────────────────────────────────────┤
│  Controllers (REST API)                                 │
│  ├─ RepositoryController                               │
│  ├─ AnalysisController                                 │
│  └─ AdminController                                    │
├─────────────────────────────────────────────────────────┤
│  Services (Business Logic)                              │
│  ├─ AnalysisService (orchestrates analysis)             │
│  ├─ GitService (clones repositories)                   │
│  ├─ LLMService (manages LLM providers)                  │
│  │   ├─ OpenRouterProvider                             │
│  │   └─ GeminiProvider                                 │
│  └─ ScheduledAnalysisService (automated jobs)           │
├─────────────────────────────────────────────────────────┤
│  Repositories (Data Access)                              │
│  ├─ CodeRepositoryRepository                            │
│  ├─ AnalysisRepository                                  │
│  └─ AnalysisResultRepository                           │
├─────────────────────────────────────────────────────────┤
│  Entities (JPA)                                          │
│  ├─ CodeRepository                                      │
│  ├─ Analysis                                            │
│  └─ AnalysisResult                                      │
├─────────────────────────────────────────────────────────┤
│  Config                                                 │
│  ├─ DataLoader (seed data & project metadata)            │
│  └─ SecurityConfig (CORS, security)                     │
└─────────────────────────────────────────────────────────┘
```

## Frontend Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    React Application                     │
├─────────────────────────────────────────────────────────┤
│  Pages                                                   │
│  ├─ Landing.tsx (home page with repository list)        │
│  └─ Demo.tsx (analysis results display)                 │
├─────────────────────────────────────────────────────────┤
│  Components (shadcn/ui)                                  │
│  ├─ Button, Card, Badge, etc.                           │
└─────────────────────────────────────────────────────────┘
```
