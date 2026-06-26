<template>
  <div class="home">
    <h2>Auto Project</h2>
    <p class="subtitle">AI-driven full-stack application powered by WorkBuddy</p>
    <el-row :gutter="20" class="cards">
      <el-col :span="8">
        <el-card>
          <template #header><span>前端</span></template>
          <p>Vue 3 + Element Plus + Vite + TypeScript</p>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header><span>后端</span></template>
          <p>Spring Boot 3.4 + Java 21 + MyBatis-Plus</p>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header><span>流水线</span></template>
          <p>每日 02:00 自动开发循环</p>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
/* ============================================================
   HomePage — Dashboard (frosted glass design)
   ============================================================ */
.home {
  text-align: center;
  padding: 60px 20px;
}

/* Page title — white on gradient background */
.home h2 {
  font-size: 28px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 12px;
  letter-spacing: -0.5px;
  animation: slideUp 0.6s ease-out;
}

/* Subtitle */
.subtitle {
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
  margin-bottom: 40px;
  animation: slideUp 0.6s ease-out 0.1s both;
}

/* Card grid */
.cards {
  max-width: 900px;
  margin: 0 auto;
}

/* Card entrance animation */
.cards :deep(.el-card) {
  text-align: left;
  animation: slideUp 0.6s ease-out both;
}

/* Staggered card animations */
.cards :deep(.el-col:nth-child(1) .el-card) {
  animation-delay: 0.15s;
}
.cards :deep(.el-col:nth-child(2) .el-card) {
  animation-delay: 0.25s;
}
.cards :deep(.el-col:nth-child(3) .el-card) {
  animation-delay: 0.35s;
}

/* Card header — brand accent */
.cards :deep(.el-card__header) {
  color: #667eea;
  font-weight: 600;
  font-size: 16px;
  border-bottom: 1px solid rgba(102, 126, 234, 0.12);
}

/* Card body text */
.cards :deep(.el-card__body) {
  color: #1a1a2e;
  font-size: 14px;
  line-height: 1.6;
}

/* ============================================================
   Keyframes (matches LoginPage)
   ============================================================ */
@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
