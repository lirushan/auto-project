# Auto Project - 全栈开发规范

## 项目信息

- 项目名称：Auto Project (auto-project)
- 技术栈：Spring Boot 3.4.7, Java 21, MySQL 8.0, MyBatis-Plus 3.5.10, Redis 7
- 前端：Vue 3 + Vite 5 + TypeScript + Element Plus + Vue Router 4 + Pinia + Axios
- 包名：com.example.autoproject
- 数据库：MySQL 8.0 (生产) / H2 (本地开发)
- 部署：Docker Compose (MySQL + Redis + Spring Boot + Nginx)
- 规范参考：Jakarta EE, 阿里巴巴 Java 开发手册

## 项目结构
```
auto-project/
├── backend/                           # Spring Boot 后端
│   └── src/main/java/com/example/autoproject/
│       ├── common/                    # Result<T>, BusinessException, GlobalExceptionHandler
│       ├── config/                    # DataSourceConfig, 各模块配置
│       ├── controller/                # @RestController
│       ├── service/                   # Service 接口
│       │   └── impl/                  # Service 实现
│       ├── mapper/                    # MyBatis-Plus Mapper
│       ├── entity/                    # 数据库实体
│       ├── dto/                       # 请求参数（Java record）
│       └── vo/                        # 返回视图对象（Java record）
│   └── src/main/resources/
│       ├── application.yml            # 通用配置
│       ├── application-dev.yml        # 本地开发 (H2)
│       ├── application-docker.yml     # Docker 环境 (MySQL + Redis)
│       └── db/schema.sql             # 数据库初始化脚本
├── frontend/                          # Vue 3 前端
│   └── src/
│       ├── api/                       # Axios API 封装
│       │   ├── index.ts               # Axios 实例（拦截器、错误处理）
│       │   └── *.ts                   # 各模块 API
│       ├── views/                     # 页面组件
│       │   ├── Layout.vue             # 公共布局（左侧菜单）
│       │   └── *.vue                  # 各功能页面
│       └── router/index.ts            # Vue Router 路由
├── docker-compose.yml                 # Docker 编排
├── docker-data/mysql/init/            # MySQL 初始化 SQL
└── .env.example                       # 环境变量模板
```

## 后端规范

### 三层架构
- Controller → Service（接口 extends IService<Entity>）→ ServiceImpl（extends ServiceImpl<Mapper, Entity>）→ Mapper（extends BaseMapper<Entity>）
- 依赖注入统一使用 `@RequiredArgsConstructor` 构造器注入，禁止 `@Autowired`
- Service 接口放 `service` 包，实现类放 `service/impl` 包

### 统一响应
- 所有 Controller 返回 `Result<T>`，结构：`{ code: 200, message: "success", data: ... }`
- 异常统一由 `GlobalExceptionHandler` 处理
- 业务异常抛出 `BusinessException(code, message)`，404 场景用 `BusinessException(404, "xxx not found")`

### 命名规范
- 类名：大驼峰，望文知义
- 方法名、变量名：小驼峰，严禁拼音、严禁无意义简写
- 常量：全大写 + 下划线
- 包名：全小写
- 表名：系统表 `sys_` 前缀（如 sys_user），业务表 `biz_` 前缀

### DTO / VO
- 前端传入参数用 DTO，返回数据用 VO
- 优先使用 Java 21 **record** 类型
- 参数校验使用 Jakarta Bean Validation（`@NotBlank`、`@NotNull`、`@Valid`）

### Controller
- 统一 `@RestController` + `@RequestMapping("/api/v1/{资源名}")`
- RESTful：GET 查询、POST 创建、PUT 更新、DELETE 删除
- 分页查询：`@RequestParam Integer page, Integer size`，默认 page=1 size=10

### Entity
- 使用 `@TableName` 指定表名，逻辑删除用 `@TableLogic`
- 主键 `@TableId(type = IdType.AUTO)`，Long 类型自增
- 时间类型统一 `LocalDateTime`
- MyBatis-Plus 配置 `map-underscore-to-camel-case: true`

### 数据库
- 表名小写 + 下划线，字段名下划线
- 索引命名：`idx_{表}_{字段}`，唯一索引：`uk_{表}_{字段}`
- Schema 初始化脚本放 `src/main/resources/db/schema.sql`
- dev 环境用 H2 内存数据库，docker 环境用 MySQL

### 代码风格
- 缩进 4 空格，大括号不换行
- 运算符两侧空格，方法间空行
- 公共方法加注释，复杂逻辑加行内注释
- 魔法值全部抽为常量

## 前端规范

### 目录结构
- `views/` — 页面组件，按功能命名（如 UserListPage.vue）
- `api/` — Axios 实例 + 各模块 API 封装
- `router/` — Vue Router 配置，懒加载

### API 调用
- 统一使用 `src/api/index.ts` 的 Axios 实例（baseURL: /api/v1）
- 拦截器自动解包 `data` 字段，错误自动 `ElMessage.error` 提示
- 每个模块独立 API 文件（如 `user.ts`），导出类型和函数

### 组件规范
- 使用 Element Plus 组件（ElTable、ElDialog、ElForm 等）
- `<script setup lang="ts">` 语法
- 表格分页用 `el-pagination`，表单校验用 `el-form` rules
- 确认删除用 `ElMessageBox.confirm`

### 状态管理
- 简单状态用组件内 `ref` / `reactive`
- 跨组件状态用 Pinia store
- 不做过度设计

## 权限设计

### RBAC 模型
- 核心表：`sys_user` / `sys_role` / `sys_menu` / `sys_user_role` / `sys_role_menu`
- 支持一个用户多个角色，登录返回角色列表，前端可切换角色
- 菜单类型：0=目录，1=菜单，2=按钮/权限
- 用户-角色-菜单三层独立，不耦合

### JWT 认证
- 使用 Spring Security + JWT 令牌
- JwtTokenProvider 从 `application.yml` 读取 `jwt.secret` 和 `jwt.expiration`
- 令牌解析用 JwtUtils 静态工具方法，令牌生成用 JwtTokenProvider（Spring Bean）
- 登录接口 `/api/v1/auth/login` 返回 token + 角色列表
- 前端 Axios 拦截器自动在请求头注入 `Authorization: Bearer {token}`

## 操作日志

- 使用自定义 `@OperationLog` 注解标记需要记录的方法
- 注解支持 SpEL 模板：`content = "保存用户 #{#dto.username}"`
- 复杂场景通过 `OperationLogContext.setContent()` 手动设置（ThreadLocal）
- AOP 切面自动写入 `sys_operation_log` 表，finally 中保证 ThreadLocal 清理

## 对象转换

- 使用 Hutool 的 `BeanUtil.copyProperties` / `BeanUtil.copyToList` 做 Entity → VO 转换
- 集合判空使用 `CollectionUtils.isEmpty`，空集合返回 `Collections.emptyList()`
- 防御性编程：外部方法返回的集合传给工具类前先判空

## Excel 导入导出

- 使用 Apache POI 或 EasyExcel（首选 EasyExcel）
- 导出 VO 使用 `@ExcelProperty` + `@ColumnWidth` 注解
- 统一通过 `ExcelUtils` 工具类封装导入导出
- 大文件导出使用流式写入，避免内存溢出

## Redis Key 管理

- 命名规范：`{auto-project}:{模块}:{类型}:{标识}`
- 禁止硬编码 Redis key，统一使用常量类管理
- 示例：`auto-project:user:token:{userId}`、`auto-project:cache:dict:{type}`

## Docker 规范
- `docker compose up -d` 一键启动（MySQL + Redis + App + Frontend）
- `docker compose down` 停止，`docker compose down -v` 清除数据卷
- `docker compose up -d --build` 重新构建并启动
- 环境变量通过 `.env` 文件注入（已加入 .gitignore）

## 自动化流水线
- 每日 02:00 自动执行（automation-1782194315595）
- 飞书发送触发词即时执行（automation-1782267998343）
- Issue 标签状态机：ready-for-dev → ai-in-progress → awaiting-review / ai-failed
