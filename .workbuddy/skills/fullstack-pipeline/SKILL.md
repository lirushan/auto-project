---
name: fullstack-pipeline
description: >
  This skill defines the full-stack development pipeline for automated, unattended
  development workflows. It should be used when building or modifying any application
  involving Spring Boot backend + React frontend, running automated tests, or deploying
  to CloudStudio/CloudBase. Triggers include: creating new features, fixing bugs from
  GitHub Issues, automated daily development cycles, and any task requiring end-to-end
  development from code to deployment.
agent_created: true
---

# Full-Stack Autonomous Development Pipeline

## Overview

This skill encodes the complete development pipeline from requirement intake to production deployment.
It is designed for unattended 7x24 autonomous execution via WorkBuddy Automations and Agent Teams.

## Tech Stack (Non-Negotiable)

### Backend
- **Language**: Java 21
- **Framework**: Spring Boot 3.4.x (latest stable)
- **ORM**: MyBatis-Plus 3.5.x
- **Build Tool**: Maven (preferred) or Gradle
- **Database**: MySQL 8.0+ / PostgreSQL 16+
- **Cache**: Redis 7.x (optional, via Spring Cache)
- **Test**: JUnit 5 + Mockito + Spring Boot Test

### Frontend
- **Framework**: React 19 + Vite 6
- **Language**: TypeScript 5.x (strict mode)
- **Styling**: Tailwind CSS 4.x
- **Component Library**: shadcn/ui (latest)
- **State Management**: Zustand or TanStack Query
- **Router**: React Router v7
- **Test**: Vitest + React Testing Library
- **E2E**: Playwright

### Deployment
- **Frontend**: CloudStudio (static deployment)
- **Backend**: CloudBase (cloud functions / container)
- **Database**: CloudBase MySQL or provisioned via MCP

## Core Capabilities

### 1. Requirement Intake

When triggered (via Automation or manual command), the pipeline:
1. Connects to GitHub via MCP to fetch Issues labeled `ready-for-dev`
2. Parses each Issue into structured requirements
3. Creates Task items for: FE development, BE development, Testing, Deployment
4. Updates Issue status to `in-progress`

### 2. Plan Mode Execution

Before writing any code:
1. Analyze the requirement holistically
2. Design API contracts between frontend and backend
3. Define database schema changes (if any)
4. Break down into parallel-executable subtasks
5. Assign subtasks to specialized agents (FE Agent, BE Agent)

### 3. Backend Development Standards

Load `references/backend-standards.md` for details. Key rules:
- Package structure: `com.example.{module}.{controller|service|mapper|entity|dto|config}`
- Controller: RESTful API, `@RestController`, return `Result<T>` unified response
- Service: Interface + Impl pattern, `@Transactional` on write operations
- Entity: MyBatis-Plus annotations, `@TableName`, `@TableId`, logical delete `@TableLogic`
- DTO: Use `records` where possible (Java 21 feature)
- Validation: Jakarta Bean Validation (`@Valid`, `@NotBlank`, etc.)
- Exception: Global `@RestControllerAdvice` handler
- Configuration: `application.yml` with profiles (dev/test/prod)
- Tests: Unit test every Service method, integration test every Controller endpoint

### 4. Frontend Development Standards

Load `references/frontend-standards.md` for details. Key rules:
- Directory: `src/{components|pages|hooks|lib|types|store}`
- Components: Functional components only, TypeScript strict mode
- Styling: Tailwind utility classes, shadcn/ui for complex components
- API calls: Centralized `lib/api.ts` with typed fetch/axios wrappers
- State: Zustand stores in `store/`, TanStack Query for server state
- Routing: Lazy-loaded routes in `src/router.tsx`
- Error handling: Error boundary at page level
- Tests: Vitest for unit, React Testing Library for component, Playwright for E2E

### 5. Automated Testing

After code generation:
1. Run `./mvnw test` (backend unit tests) — must pass 100%
2. Run `npm test` (frontend unit tests) — must pass 100%
3. Run Playwright E2E tests against deployed preview
4. If any test fails: log failure, auto-fix the code, re-run tests (max 3 retries)
5. Run `npm run lint` — zero warnings
6. Run `./mvnw checkstyle:check` — zero violations

### 6. Deployment

After all tests pass:
1. Build frontend: `npm run build` → deploy `dist/` to CloudStudio
2. Build backend: `./mvnw package -DskipTests` → deploy JAR to CloudBase
3. Verify deployment health (HTTP 200 on health endpoint)
4. Capture deployment URLs

### 7. Acceptance & Notification

1. Run smoke tests against deployed URLs
2. Generate a brief acceptance report (pass/fail, test coverage, URLs)
3. Send report to **DingTalk (钉钉)** via MCP connector
4. Update GitHub Issue: close with comment containing deployment URLs and report summary
5. Archive all artifacts (code, test reports, deployment logs) to project workspace

### 8. Failure Recovery

If any phase fails:
1. Log the error with full stack trace
2. Auto-attempt fix (up to 3 iterations)
3. If still failing after 3 attempts: **escalate** — send DingTalk alert with error details, do NOT proceed to deployment

## Resources

### references/
- `tech-stack.md` — Complete version-pinned dependency list
- `backend-standards.md` — Spring Boot coding conventions and patterns
- `frontend-standards.md` — React/TypeScript coding conventions and patterns
- `deployment.md` — CloudStudio and CloudBase deployment procedures
