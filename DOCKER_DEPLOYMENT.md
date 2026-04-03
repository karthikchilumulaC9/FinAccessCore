# Docker Deployment Guide

## Overview
This guide explains how to build and run FinAccessCore using Docker and Docker Compose.

---

## Prerequisites

- Docker Desktop (Windows/Mac) or Docker Engine (Linux)
- Docker Compose v2.0+
- At least 2GB of available RAM
- Ports 8082 and 3307 available

### Check Docker Installation
```bash
docker --version
docker-compose --version
```

---

## Quick Start

### 1. Build and Run with Docker Compose (Recommended)

```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Stop and remove volumes (clean slate)
docker-compose down -v
```

### 2. Access the Application

- **API Base URL**: http://localhost:8082
- **MySQL Database**: localhost:3307
- **Health Check**: http://localhost:8082/actuator/health

### 3. Test the API

```bash
# Login as admin
curl -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'

# Use the token from response
export TOKEN="your-jwt-token-here"

# Get dashboard summary
curl -X GET http://localhost:8082/api/dashboard/summary \
  -H "Authorization: Bearer $TOKEN"
```

---

## Docker Architecture

### Services

1. **mysql** - MySQL 8.0 database
   - Port: 3307 (host) → 3306 (container)
   - Database: finaccessdb
   - User: finaccess_user
   - Auto-initializes with schema.sql and data.sql

2. **app** - Spring Boot application
   - Port: 8082 (host) → 8082 (container)
   - Depends on MySQL (waits for health check)
   - Runs as non-root user for security

### Volumes

- **mysql_data**: Persists MySQL data across container restarts

### Networks

- **finaccesscore-network**: Bridge network for service communication

---

## Building Docker Image

### Build with Docker Compose
```bash
docker-compose build
```

### Build Manually
```bash
# Build the image
docker build -t finaccesscore:latest .

# Run the container (requires MySQL)
docker run -d \
  -p 8082:8082 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/finaccessdb \
  -e SPRING_DATASOURCE_USERNAME=finaccess_user \
  -e SPRING_DATASOURCE_PASSWORD=finaccess_password \
  --name finaccesscore-app \
  finaccesscore:latest
```

---

## Configuration

### Environment Variables

You can override configuration by setting environment variables in `docker-compose.yml`:

```yaml
environment:
  # Database
  SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/finaccessdb
  SPRING_DATASOURCE_USERNAME: finaccess_user
  SPRING_DATASOURCE_PASSWORD: finaccess_password
  
  # JWT
  JWT_SECRET: your-secret-key-min-256-bits
  JWT_EXPIRATION: 86400000
  
  # Server
  SERVER_PORT: 8082
  
  # Profile
  SPRING_PROFILES_ACTIVE: prod
```

### Production Configuration

For production, update these values:

1. **Change MySQL passwords**:
   ```yaml
   MYSQL_ROOT_PASSWORD: <strong-password>
   MYSQL_PASSWORD: <strong-password>
   ```

2. **Change JWT secret**:
   ```yaml
   JWT_SECRET: <generate-strong-secret-key>
   ```

3. **Use environment-specific profiles**:
   ```yaml
   SPRING_PROFILES_ACTIVE: prod
   ```

4. **Enable SSL for MySQL**:
   ```yaml
   SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/finaccessdb?useSSL=true
   ```

---

## Docker Commands

### Service Management

```bash
# Start services
docker-compose up -d

# Stop services
docker-compose stop

# Restart services
docker-compose restart

# Remove services
docker-compose down

# Remove services and volumes
docker-compose down -v
```

### Logs and Monitoring

```bash
# View all logs
docker-compose logs

# Follow logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f app
docker-compose logs -f mysql

# View last 100 lines
docker-compose logs --tail=100 app
```

### Container Management

```bash
# List running containers
docker-compose ps

# Execute command in container
docker-compose exec app sh
docker-compose exec mysql mysql -u root -p

# View container stats
docker stats
```

### Database Access

```bash
# Connect to MySQL
docker-compose exec mysql mysql -u finaccess_user -pfinaccess_password finaccessdb

# Backup database
docker-compose exec mysql mysqldump -u root -prootpassword finaccessdb > backup.sql

# Restore database
docker-compose exec -T mysql mysql -u root -prootpassword finaccessdb < backup.sql
```

---

## Health Checks

### Application Health
```bash
curl http://localhost:8082/actuator/health
```

### MySQL Health
```bash
docker-compose exec mysql mysqladmin ping -h localhost -u root -prootpassword
```

### Container Health Status
```bash
docker-compose ps
```

---

## Troubleshooting

### Application Won't Start

1. **Check logs**:
   ```bash
   docker-compose logs app
   ```

2. **Verify MySQL is healthy**:
   ```bash
   docker-compose ps mysql
   ```

3. **Check database connection**:
   ```bash
   docker-compose exec mysql mysql -u finaccess_user -pfinaccess_password -e "SHOW DATABASES;"
   ```

### Port Already in Use

```bash
# Change ports in docker-compose.yml
ports:
  - "8083:8082"  # Change host port
  - "3308:3306"  # Change MySQL host port
```

### Database Connection Issues

1. **Wait for MySQL to be ready**:
   ```bash
   docker-compose logs mysql | grep "ready for connections"
   ```

2. **Verify network connectivity**:
   ```bash
   docker-compose exec app ping mysql
   ```

3. **Check environment variables**:
   ```bash
   docker-compose exec app env | grep SPRING_DATASOURCE
   ```

### Out of Memory

```bash
# Increase Docker memory limit in Docker Desktop settings
# Or add memory limits to docker-compose.yml:
services:
  app:
    deploy:
      resources:
        limits:
          memory: 1G
```

### Clean Slate Restart

```bash
# Remove everything and start fresh
docker-compose down -v
docker system prune -a
docker-compose up -d
```

---

## Performance Optimization

### 1. Multi-stage Build
The Dockerfile uses multi-stage builds to minimize image size:
- Build stage: Maven + JDK (large)
- Runtime stage: JRE only (small)

### 2. Layer Caching
Dependencies are downloaded in a separate layer for faster rebuilds.

### 3. Health Checks
Both services have health checks to ensure proper startup order.

### 4. Resource Limits
Add resource limits for production:

```yaml
services:
  app:
    deploy:
      resources:
        limits:
          cpus: '1.0'
          memory: 1G
        reservations:
          cpus: '0.5'
          memory: 512M
```

---

## Security Best Practices

### 1. Non-root User
The application runs as a non-root user inside the container.

### 2. Secret Management
For production, use Docker secrets or environment variables from a secure source:

```bash
# Using environment file
docker-compose --env-file .env.prod up -d
```

### 3. Network Isolation
Services communicate over a private bridge network.

### 4. Read-only Filesystem
Add read-only filesystem for enhanced security:

```yaml
services:
  app:
    read_only: true
    tmpfs:
      - /tmp
```

---

## CI/CD Integration

### GitHub Actions Example

```yaml
name: Build and Push Docker Image

on:
  push:
    branches: [ master ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Build Docker image
        run: docker build -t finaccesscore:${{ github.sha }} .
      
      - name: Push to registry
        run: |
          echo ${{ secrets.DOCKER_PASSWORD }} | docker login -u ${{ secrets.DOCKER_USERNAME }} --password-stdin
          docker push finaccesscore:${{ github.sha }}
```

---

## Production Deployment

### 1. Use Docker Swarm or Kubernetes
For production, consider orchestration platforms:

```bash
# Docker Swarm
docker stack deploy -c docker-compose.yml finaccesscore

# Kubernetes
kubectl apply -f k8s/
```

### 2. Use External Database
Point to a managed database service (AWS RDS, Azure Database, etc.):

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:mysql://your-rds-endpoint:3306/finaccessdb
```

### 3. Enable HTTPS
Use a reverse proxy (Nginx, Traefik) for SSL termination.

### 4. Monitoring
Add monitoring tools:
- Prometheus for metrics
- Grafana for dashboards
- ELK stack for logs

---

## Image Size Optimization

Current image size: ~200MB (JRE + application)

### Further Optimization

1. **Use Alpine-based images** (already implemented)
2. **Remove unnecessary dependencies** from pom.xml
3. **Use jlink for custom JRE**:
   ```dockerfile
   RUN jlink --add-modules java.base,java.sql,java.naming \
       --output /custom-jre
   ```

---

## Default Credentials

**Admin User**:
- Username: `admin`
- Password: `password123`

**MySQL**:
- Root Password: `rootpassword`
- User: `finaccess_user`
- Password: `finaccess_password`

⚠️ **Change these in production!**

---

## Support

For issues or questions:
1. Check logs: `docker-compose logs -f`
2. Review this guide
3. Check GitHub issues
4. Contact support team

---

## Summary

```bash
# Quick reference
docker-compose up -d          # Start
docker-compose logs -f        # View logs
docker-compose ps             # Check status
docker-compose down           # Stop
docker-compose down -v        # Clean slate
```

Your FinAccessCore application is now containerized and ready for deployment! 🚀
