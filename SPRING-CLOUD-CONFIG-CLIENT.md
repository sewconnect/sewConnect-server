## Spring Cloud Config

This application uses Spring Cloud Config Client to fetch configuration from a central config server.

### Configuration
- Config Server URL: `http://localhost:8888` (default)
- Application name: `sew-connect-platform`
- Fail-fast disabled: Application will start even if config server is unavailable

### Local Development
For local development, the config server connection is optional. The application will use local `application.properties` if the config server is not available.

### Production
Set the config server URL via environment variable:
```bash
SPRING_CLOUD_CONFIG_URI=https://config-server.example.com
```