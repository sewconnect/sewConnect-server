#!/usr/bin/env bash
set -euo pipefail

COMPOSE_FILE=docker-compose.test.yml

echo "Starting test environment..."

# Start test services
docker-compose -f "$COMPOSE_FILE" up -d

echo "Waiting for services to be ready..."

# Wait for Postgres to be ready (simpler approach)
for i in $(seq 1 30); do
  if docker-compose -f "$COMPOSE_FILE" exec -T postgres pg_isready -U test_user -d sewconnect_test >/dev/null 2>&1; then
    echo "✅ PostgreSQL is ready!"
    break
  fi
  echo "Waiting for PostgreSQL... ($i/30)"
  sleep 2
done

# Optional: Wait for RabbitMQ
for i in $(seq 1 30); do
  if docker-compose -f "$COMPOSE_FILE" exec -T rabbitmq rabbitmq-diagnostics ping >/dev/null 2>&1; then
    echo "RabbitMQ is ready!"
    break
  fi
  echo "Waiting for RabbitMQ... ($i/30)"
  sleep 2
done

# Run tests
echo "Running tests..."
mvn clean test
RC=$?

# Tear down
echo "Cleaning up..."
docker-compose -f "$COMPOSE_FILE" down -v

if [ $RC -eq 0 ]; then
  echo "All tests passed!"
else
  echo "Tests failed!"
fi

exit $RC