# Local Development Guide

## Running the Application Locally

### Option 1: Using Maven with Local Profile 
```bash
mvn spring-boot:run -Plocal
```

This will:
- Use the `local` profile configuration
- Connect to local PostgreSQL and RabbitMQ (via Docker)
- Disable Cloudinary
- Use mock credentials for external services

### Option 2: Start Dependencies First
```bash
# Start PostgreSQL and RabbitMQ
docker-compose up -d postgres rabbitmq

# Run the application
mvn spring-boot:run -Plocal
```

### Option 3: Using IDE

Set the active profile in your IDE:
- **IntelliJ IDEA**: Run → Edit Configurations → Active Profiles: `local`
- **VS Code**: In `launch.json`, add `"spring.profiles.active": "local"`

## Configuration

### Local Profile (`application-local.yml`)
- PostgreSQL: `localhost:5432`
- RabbitMQ: `localhost:5672`
- Cloudinary: Disabled
- Mock JWT secret (not for production!)

### Creating Personal Overrides

For personal settings, create `application-local-override.yml` (gitignored):
```yaml
# Your personal overrides
server:
  port: 8080  # Change port if needed

spring:
  datasource:
    url: jdbc:postgresql://my-custom-host:5432/mydb
```

## Testing Different Profiles
```bash
# Local development
mvn spring-boot:run -Plocal

# With Cloudinary enabled
mvn spring-boot:run -Dspring.profiles.active=local,cloudinary

# Production-like
mvn spring-boot:run -Pprod
```

## Troubleshooting

### Application won't start
1. Ensure PostgreSQL is running: `docker-compose ps`
2. Check port availability: `lsof -i :6666`
3. Verify Java version: `java -version` (should be 17+)

### Database connection issues
```bash
# Check PostgreSQL logs
docker-compose logs postgres

# Recreate database
docker-compose down -v
docker-compose up -d postgres
```

### RabbitMQ connection issues
```bash
# Check RabbitMQ status
docker-compose logs rabbitmq

# Access management UI
# http://localhost:15672 (guest/guest)
```
