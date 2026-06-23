# 项目记忆

## 技术栈（实际版本）
- 后端：Spring Boot 3.4.7 + Java 21 + MyBatis-Plus 3.5.10 + MySQL 8.0 / H2(dev)
- 前端：React 19.2.x + Vite 8.x + TypeScript 6.0 + Tailwind CSS 4.x + shadcn/ui
- 代码仓库：GitHub（已连接）
- 通知渠道：飞书（已连接）
- Maven：D:\tools\apache-maven-3.9.9
- JAVA_HOME：C:\Program Files\Java\jdk-21

## 项目结构
```
D:\code\auto-project\
├── backend/                  # Spring Boot 后端
│   ├── pom.xml
│   ├── mvnw.cmd
│   └── src/main/java/com/example/autoproject/
│       ├── AutoProjectApplication.java
│       ├── common/           # Result, BusinessException, GlobalExceptionHandler
│       ├── controller/       # HealthController
│       ├── service/          # (待扩展)
│       ├── mapper/           # MyBatis-Plus mapper
│       └── entity/           # 数据库实体
├── frontend/                 # React + Vite 前端
│   ├── src/
│   │   ├── components/       # AppLayout
│   │   ├── pages/            # HomePage
│   │   ├── lib/              # api.ts, utils.ts
│   │   └── router.tsx        # React Router
│   └── dist/                 # 构建产物
└── .workbuddy/               # WorkBuddy 配置（Skill + Memory）
```

## 自动化流水线
- **每日全栈开发流水线**（automation-1782194315595）：每天 02:00 执行，状态 ACTIVE
- 完整流程：GitHub Issue → 前后端并行 → 测试 → CloudStudio/CloudBase 部署 → 飞书通知

## 已验证
- ✅ 后端编译通过
- ✅ 后端单元测试通过（Spring Boot + H2）
- ✅ 前端构建通过（Vite + Tailwind CSS v4）
- ✅ GitHub MCP 已连接
- ✅ 飞书 MCP 已连接

## 待办
- 初始化 Git 仓库并 push 到 GitHub
- 在 GitHub 创建 `ready-for-dev` label
