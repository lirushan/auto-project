# Deployment Procedures

## Frontend → CloudStudio

After `npm run build` produces the `dist/` directory:

1. Use the `cloudstudio-deploy` skill or `workbuddy_cloudstudio_deploy` tool
2. Point to the `dist/` directory
3. The tool returns a public preview URL
4. Verify the URL returns HTTP 200

```bash
npm run build
# dist/ is created
# Deploy via CloudStudio skill → returns https://xxx.cloudstudio.net
```

## Backend → CloudBase

After `./mvnw package -DskipTests` produces the JAR:

1. Use CloudBase MCP connector to deploy
2. Configure environment variables (DB connection, Redis, etc.)
3. Deploy as CloudBase Cloud Run service
4. Expose port 8080
5. Verify health endpoint: `GET /actuator/health` returns 200

```bash
./mvnw package -DskipTests
# target/app.jar is created
# Deploy via CloudBase connector
```

## Health Check

After deployment, verify:

```
GET {frontend_url}          → 200 (HTML page loads)
GET {backend_url}/actuator/health → 200 {"status":"UP"}
GET {backend_url}/api/v1/health   → 200 {"code":200,"message":"success"}
```

## Rollback

If deployment verification fails:
1. Log the failure
2. Do NOT attempt rollback automatically — the previous deployment is still live
3. Send DingTalk alert with error details
4. Keep the issue open on GitHub

## Environment Variables (Backend)

```yaml
# application-prod.yml
spring:
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/app}
    username: ${DB_USER:root}
    password: ${DB_PASSWORD:}
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
```
