# Tech Stack — Version Pinned

## Backend Dependencies (Maven pom.xml)

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.4.7</version>
</parent>

<properties>
    <java.version>21</java.version>
    <mybatis-plus.version>3.5.10</mybatis-plus.version>
    <mysql.version>8.0.33</mysql.version>
    <hutool.version>5.8.35</hutool.version>
</properties>

<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- MyBatis-Plus -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
        <version>${mybatis-plus.version}</version>
    </dependency>

    <!-- MySQL -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- Hutool -->
    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-all</artifactId>
        <version>${hutool.version}</version>
    </dependency>
</dependencies>
```

## Frontend Dependencies (package.json)

```json
{
  "dependencies": {
    "react": "^19.1.0",
    "react-dom": "^19.1.0",
    "react-router-dom": "^7.5.0",
    "zustand": "^5.0.0",
    "@tanstack/react-query": "^5.70.0",
    "axios": "^1.8.0",
    "lucide-react": "^0.500.0",
    "tailwindcss": "^4.1.0",
    "class-variance-authority": "^0.7.0",
    "clsx": "^2.1.0",
    "tailwind-merge": "^3.2.0"
  },
  "devDependencies": {
    "@vitejs/plugin-react": "^4.4.0",
    "vite": "^6.3.0",
    "typescript": "^5.8.0",
    "@types/react": "^19.1.0",
    "@types/react-dom": "^19.1.0",
    "vitest": "^3.1.0",
    "@testing-library/react": "^16.3.0",
    "@testing-library/jest-dom": "^6.6.0",
    "jsdom": "^26.0.0",
    "eslint": "^9.24.0",
    "@eslint/js": "^9.24.0",
    "typescript-eslint": "^8.30.0",
    "eslint-plugin-react-hooks": "^5.2.0",
    "eslint-plugin-react-refresh": "^0.4.0",
    "prettier": "^3.5.0",
    "prettier-plugin-tailwindcss": "^0.6.0"
  }
}
```

## shadcn/ui Setup

Use `npx shadcn@latest init` with:
- Style: New York
- Base color: Neutral
- CSS variables: Yes (Tailwind v4)

## Database

- MySQL 8.0+ with `utf8mb4` charset and `utf8mb4_unicode_ci` collation
- MyBatis-Plus auto-fill for `create_time` / `update_time`
- Logical delete enabled globally via `mybatis-plus.global-config.db-config.logic-delete-field=deleted`
