# Docker Configuration

## docker-compose.yml

Defines the production configuration:

- **postgres**: PostgreSQL 15 database
  - Port: 5432
  - Environment: POSTGRES_DB, POSTGRES_USER, POSTGRES_PASSWORD
  - Volume: PostgreSQL data persistence

- **redis**: Redis 7 cache
  - Port: 6379
  - Volume: Redis data persistence

- **backend**: Spring Boot application
  - Port: 8080
  - Build: Multi-stage Docker build
  - Environment: Database URL, Redis host, API keys
  - Depends on: postgres, redis

- **frontend**: React application
  - Port: 80 (production build)
  - Build: Multi-stage Docker build
  - Depends on: backend

- **nginx**: Nginx reverse proxy
  - Ports: 80, 443
  - Configuration: nginx/nginx.conf
  - Depends on: frontend, backend

- **pgadmin**: pgAdmin 4 database UI
  - Port: 5050
  - Environment: PGADMIN_DEFAULT_EMAIL, PGADMIN_DEFAULT_PASSWORD
  - Depends on: postgres

## docker-compose.override.yml

Defines development overrides:

- **frontend**: Overrides to use Dockerfile.dev
  - Port: 8000 (Vite dev server)
  - Volumes: Hot reload mount
  - Command: npm run dev

## Dockerfile (Backend)

Multi-stage build:
1. **Build stage**: Maven build with dependencies
2. **Run stage**: JRE 21 with application JAR

## Dockerfile.dev (Frontend)

Development build:
- Node.js 20 base image
- Install dependencies
- Run Vite dev server with host binding
