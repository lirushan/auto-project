---
name: fullstack-pipeline
description: >
  This skill defines the full-stack development pipeline for automated, unattended
  development workflows. It should be used when building or modifying any application
  involving Spring Boot backend + Vue 3 frontend, running automated tests, or deploying
  via Docker. Triggers include: creating new features, fixing bugs from GitHub Issues,
  automated daily development cycles, and any task requiring end-to-end development.
agent_created: true
---

# Full-Stack Autonomous Development Pipeline

## Overview

This skill encodes the complete development pipeline from requirement intake to production deployment.
It is designed for unattended 7x24 autonomous execution via WorkBuddy Automations and Agent Teams.

## ⚠️ Before Writing Any Code (Mandatory)

**Always read `references/coding-standards.md` first.** It contains all coding conventions: naming, table prefixes (sys_/biz_), package structure, API patterns, component rules, and RBAC design. Never skip this step.

## Tech Stack (Non-Negotiable)

### Backend
- Java 21 + Spring Boot 3.4.7
- MyBatis-Plus 3.5.10
- MySQL 8.0 (production) / H2 (dev)
- Redis 7 (Docker)
- Maven: `D:\tools\apache-maven-3.9.9\bin\mvn.cmd`
- JAVA_HOME: `C:\Program Files\Java\jdk-21`

### Frontend
- Vue 3 + Vite 5 + TypeScript ~5.6
- Element Plus (latest) + @element-plus/icons-vue
- Vue Router 4 + Pinia + Axios

### Deployment
- Docker Compose: `docker compose up -d --build`
- Frontend: Nginx on port 8081, reverse proxy /api → app:8080
- Backend: Spring Boot on port 8080
- MySQL on port 3307, Redis on port 6380

## Core Flow

1. **Intake**: Fetch Issue with `ready-for-dev` label → change to `ai-in-progress`
2. **Plan**: Read Issue → design API contract → create subtasks
3. **Develop**: FE Agent + BE Agent in parallel
4. **Test**: `mvn test` + `npm run build`, max 3 retries
5. **Deploy**: `docker compose up -d --build` → health check
6. **Notify**: gh issue edit → `awaiting-review` + comment + Feishu reply

## Resources

### references/
- `coding-standards.md` — **Complete coding conventions** (MUST read before coding)
- `tech-stack.md` — version-pinned dependency list
- `deployment.md` — deployment procedures
