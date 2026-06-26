<template>
  <div class="student-layout">
    <header class="top-nav">
      <div class="nav-left">
        <span class="logo">暑托班</span>
        <nav class="nav-links">
          <router-link to="/student" class="nav-item" active-class="active">课程报名</router-link>
          <router-link to="/student/result" class="nav-item" active-class="active">我的结果</router-link>
        </nav>
      </div>
      <div class="nav-right">
        <span class="phone">{{ phone }}</span>
        <el-button type="danger" plain size="small" @click="handleLogout">退出</el-button>
      </div>
    </header>
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const phone = ref('')

onMounted(() => {
  const info = localStorage.getItem('student_info')
  if (info) {
    try { phone.value = JSON.parse(info).phone || '' } catch { /* ignore */ }
  }
})

function handleLogout() {
  localStorage.removeItem('student_token')
  localStorage.removeItem('student_info')
  router.push('/student/login')
}
</script>

<style scoped>
.student-layout {
  min-height: 100vh;
  background: #f5f7fa;
}
.top-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
  padding: 0 32px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}
.nav-left {
  display: flex;
  align-items: center;
  gap: 40px;
}
.logo {
  font-size: 20px;
  font-weight: 700;
  color: #0d9488;
  letter-spacing: 2px;
}
.nav-links {
  display: flex;
  gap: 8px;
}
.nav-item {
  padding: 6px 16px;
  border-radius: 6px;
  font-size: 14px;
  color: #666;
  text-decoration: none;
  transition: all 0.2s;
}
.nav-item:hover { background: #f0fdf4; color: #0d9488; }
.nav-item.active { background: #ecfdf5; color: #0d9488; font-weight: 600; }
.nav-right {
  display: flex;
  align-items: center;
  gap: 16px;
}
.phone { font-size: 14px; color: #333; }
.main-content {
  max-width: 1200px;
  margin: 24px auto;
  padding: 0 16px;
}
</style>
