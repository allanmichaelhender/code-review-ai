---
description: Deploy the application to GCP with HTTP then HTTPS
---

# GCP Deployment Workflow

## Prerequisites

1. GCP account with a VM instance running
2. Domain name configured with DNS pointing to the VM's public IP
3. SSH access to the VM
4. Docker and Docker Compose installed on the VM
5. Environment variables set in `.env` file:
   - `GEMINI_API_KEY`
   - `OPEN_ROUTER_API_KEY`
   - `GITHUB_API_TOKEN`
   - `PGADMIN_DEFAULT_EMAIL`
   - `PGADMIN_DEFAULT_PASSWORD`

## Architecture

**Production deployment:**

- Frontend is built during nginx Docker build and served as static files
- Frontend container exists but is not started (dev profile)
- Nginx serves static files directly and proxies API requests to backend
- Backend, PostgreSQL, Redis run as separate containers
- Database schema managed by Flyway migrations (versioned)
- Spring profile: default (no profile, uses ddl-auto: validate)

**Development deployment:**

- Frontend container runs with hot reload (Dockerfile.dev)
- Nginx proxies to frontend dev server (nginx.dev.conf)
- Backend uses dev profile (DataLoader enabled, ddl-auto: update)
- Use `docker compose -f docker-compose.yml -f docker-compose.override.yml up -d`

## Step 1: Initial HTTP Deployment

1. Clone the repository on the VM:

   ```bash
   git clone <your-repo-url>
   cd code-review-ai
   ```

2. Create `.env` file with required environment variables

3. Update `nginx/nginx.conf`:
   - Replace `your-domain.com` with your actual domain name

4. Build and start containers (production mode):

   ```bash
   docker compose up -d --build
   ```

5. Verify the application is accessible at `http://your-domain.com`

## Step 2: Obtain SSL Certificate with Certbot

1. Create certbot directories:

   ```bash
   mkdir -p certbot/conf certbot/www
   ```

2. Run certbot to obtain certificate:

   ```bash
   docker compose --profile certbot run --rm certbot certonly --webroot --webroot-path /var/www/certbot -d your-domain.com
   ```

3. Verify certificates are in `certbot/conf/live/your-domain.com/`

## Step 3: Configure HTTPS

1. Update `nginx/nginx.conf` to add HTTPS server block:
   - Add `listen 443 ssl;`
   - Add SSL certificate paths:
     - `ssl_certificate /etc/letsencrypt/live/your-domain.com/fullchain.pem;`
     - `ssl_certificate_key /etc/letsencrypt/live/your-domain.com/privkey.pem;`
   - Add HTTP to HTTPS redirect in the HTTP server block

2. Rebuild and restart nginx:

   ```bash
   docker compose up -d --build nginx
   ```

3. Verify HTTPS works at `https://your-domain.com`

## Step 4: Set Up Certificate Auto-Renewal

1. Create a renewal script:

   ```bash
   nano renew-cert.sh
   ```

2. Add the following content:

   ```bash
   #!/bin/bash
   docker compose --profile certbot run --rm certbot renew
   docker compose restart nginx
   ```

3. Make it executable:

   ```bash
   chmod +x renew-cert.sh
   ```

4. Add to crontab for daily renewal check:
   ```bash
   crontab -e
   # Add: 0 3 * * * /path/to/renew-cert.sh
   ```

## Step 5: Production Hardening

1. pgadmin is already in dev profile (not running in production)
2. Frontend is in dev profile (not running in production)
3. Change default PostgreSQL password in `.env` and docker-compose.yml
4. Configure firewall rules:
   - Allow: 80, 443
   - Block: 5432, 6379, 8080, 5050
5. Enable GCP firewall rules for only necessary ports

## Development Mode

To run with hot reload (local development):

```bash
docker compose -f docker-compose.yml -f docker-compose.override.yml up -d
```

## Troubleshooting

- **Certificate errors**: Check certbot logs with `docker compose --profile certbot logs certbot`
- **Nginx errors**: Check nginx logs with `docker compose logs nginx`
- **Container not starting**: Check logs with `docker compose logs <service>`
- **Port conflicts**: Ensure no other services are using ports 80, 443
- **Frontend not loading**: Check nginx build completed successfully with `docker compose logs nginx`
