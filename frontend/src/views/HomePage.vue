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
.home {
  text-align: center;
  padding: 60px 20px;
}
.subtitle {
  color: var(--el-text-color-secondary);
  margin-bottom: 40px;
}
.cards {
  max-width: 900px;
  margin: 0 auto;
}
</style>
