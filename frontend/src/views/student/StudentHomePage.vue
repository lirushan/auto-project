<template>
  <div class="home-page">
    <div class="banner">
      <h1>2025 暑托班报名</h1>
      <p>暑假托管 · 快乐成长 · 名额有限</p>
    </div>

    <div class="course-list" v-loading="loading">
      <div v-for="c in courses" :key="c.id" class="course-card">
        <div class="card-header">
          <h2>{{ c.courseName }}</h2>
          <el-tag v-if="enrolledIds.has(c.id)" type="success" size="large">已报名</el-tag>
          <el-tag v-else-if="c.remaining <= 0" type="danger" size="large">已满</el-tag>
          <el-tag v-else type="warning" size="large">报名中</el-tag>
        </div>
        <div class="card-body">
          <div class="info-item">
            <span class="label">课程时段</span>
            <span class="value">{{ c.timePeriod || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="label">招生名额</span>
            <span class="value">{{ c.enrolledCount || 0 }} / {{ c.quota }} 人</span>
          </div>
          <div class="info-item">
            <span class="label">剩余名额</span>
            <span class="value highlight">{{ c.remaining ?? c.quota }} 人</span>
          </div>
          <div class="info-item full" v-if="c.description">
            <span class="label">课程描述</span>
            <span class="value desc">{{ c.description }}</span>
          </div>
        </div>
        <div class="card-footer">
          <template v-if="enrolledIds.has(c.id)">
            <el-alert type="success" :closable="false" show-icon title="您已成功报名该课程" />
          </template>
          <template v-else>
            <el-button
              type="primary"
              size="large"
              @click="$router.push(`/student/enroll?courseId=${c.id}`)"
              :disabled="(c.remaining ?? c.quota) <= 0"
            >
              {{ (c.remaining ?? c.quota) > 0 ? '立即报名' : '名额已满' }}
            </el-button>
          </template>
        </div>
      </div>

      <el-empty v-if="!loading && courses.length === 0" description="暂无课程" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getCourses, getMyRegistrations } from '@/api/student'

const loading = ref(true)
const courses = ref<any[]>([])
const enrolledIds = ref(new Set<number>())

onMounted(async () => {
  try {
    const res = await getCourses()
    courses.value = Array.isArray(res) ? res : res.records || []
  } catch {
    courses.value = []
  }

  try {
    const regs = await getMyRegistrations()
    const list = Array.isArray(regs) ? regs : regs.records || []
    enrolledIds.value = new Set(list.map((r: any) => r.courseId))
  } catch {
    enrolledIds.value = new Set()
  }

  loading.value = false
})
</script>

<style scoped>
.home-page { max-width: 900px; margin: 0 auto; }
.banner {
  text-align: center;
  padding: 40px 0 32px;
}
.banner h1 {
  font-size: 32px;
  font-weight: 700;
  color: #0d9488;
  margin: 0 0 8px;
}
.banner p {
  font-size: 16px;
  color: #999;
  margin: 0;
}
.course-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.course-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 16px rgba(0,0,0,0.06);
  overflow: hidden;
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 32px;
  background: linear-gradient(135deg, #ecfdf5, #d1fae5);
  border-bottom: 1px solid #e5e7eb;
}
.card-header h2 {
  margin: 0;
  font-size: 22px;
  color: #065f46;
}
.card-body {
  padding: 32px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}
.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.info-item.full {
  grid-column: 1 / -1;
}
.label {
  font-size: 13px;
  color: #999;
}
.value {
  font-size: 18px;
  color: #333;
  font-weight: 600;
}
.value.highlight { color: #0d9488; font-size: 24px; }
.value.desc { font-size: 14px; font-weight: 400; color: #666; }
.card-footer {
  padding: 24px 32px;
  border-top: 1px solid #f0f0f0;
  text-align: center;
}
.card-footer .el-button {
  min-width: 200px;
  height: 48px;
  font-size: 16px;
  border-radius: 10px;
  background: linear-gradient(135deg, #43e97b, #38f9d7);
  border: none;
  color: #fff;
}
.card-footer .el-button:disabled {
  background: #e5e7eb;
  color: #999;
}
</style>
