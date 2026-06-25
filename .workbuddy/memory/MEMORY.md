# 项目记忆

## 技术栈（实际版本）
- 后端：Spring Boot 3.4.7 + Java 21 + MyBatis-Plus 3.5.10 + MySQL 8.0 / H2(dev)
- 前端：React 19.2.x + Vite 8.x + TypeScript 6.0 + Tailwind CSS 4.x + shadcn/ui
- 代码仓库：GitHub（已连接 + PAT 写入权限）
- 通知渠道：飞书（已连接）
- Maven：D:\tools\apache-maven-3.9.9 | JAVA_HOME：C:\Program Files\Java\jdk-21
- GitHub CLI：C:\Program Files\GitHub CLI\gh.exe（通过 GH_TOKEN PAT 认证）

## 项目仓库
- URL：https://github.com/lirushan/auto-project（公开）
- 分支：main
- Issue 标签：ready-for-dev / ai-in-progress / awaiting-review / ai-failed

## 自动化流水线
- **每日全栈开发流水线**（automation-1782194315595）：每天 02:00，ACTIVE
- Issue 方式：`gh issue list` 拉取 → `gh issue edit` 改标签 → `gh issue comment` 写验收报告
- 状态流转：ready-for-dev → ai-in-progress → awaiting-review / ai-failed

## 已完成
- ✅ 后端：User CRUD API（Entity/Mapper/Service/Controller/Test）
- ✅ 前端：UserListPage + UserDialog
- ✅ 后端测试 18/18 PASS，前端构建 PASS
- ✅ Git push 到 GitHub
- ✅ GitHub labels 创建（4个）
- ✅ GitHub PAT 配置（gh CLI 可读写 Issue）
- ✅ Issue #1 已流转至 awaiting-review

## 待办
- Issue #2：Docker 容器化（ready-for-dev，明日 02:00 自动处理）
