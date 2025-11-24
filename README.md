## Local Development

To run the application locally without production credentials:
```bash
# Start dependencies
docker-compose up -d postgres rabbitmq

# Run application with local profile
mvn spring-boot:run -Plocal
```

See [DEVELOPMENT.md](DEVELOPMENT.md) for detailed instructions.# springboot-seurity-JWT-
