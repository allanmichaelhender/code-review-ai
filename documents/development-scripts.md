# Development Scripts

## Docker Scripts

### Start all services (production mode)
```bash
docker compose up -d
```

### Start all services (development mode with hot reload)
```bash
docker compose -f docker-compose.yml -f docker-compose.override.yml up -d
```

### Stop all services
```bash
docker compose down
```

### Rebuild and restart a specific service
```bash
docker compose up -d --build backend
docker compose up -d --build frontend
```

### View logs
```bash
# All services
docker compose logs -f

# Specific service
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f postgres
```

## Backend Scripts

### Run backend locally
```bash
cd backend
mvn spring-boot:run
```

### Build backend
```bash
cd backend
mvn clean package
```

### Run tests
```bash
cd backend
mvn test
```

## Frontend Scripts

### Run frontend locally
```bash
cd frontend
npm install
npm run dev
```

### Build frontend for production
```bash
cd frontend
npm run build
```

### Run frontend type checking
```bash
cd frontend
npm run type-check
```
