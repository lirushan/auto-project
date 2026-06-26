<template>
  <div class="result-page">
    <div class="page-header">
      <h2>我的报名记录</h2>
      <p class="subtitle">查看已报名的课程及状态</p>
    </div>

    <div class="result-card" v-loading="loading">
      <template v-if="list.length > 0">
        <el-table :data="list" stripe>
          <el-table-column prop="courseName" label="课程名" />
          <el-table-column prop="rankNum" label="排名" width="80" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="statusTag(row.status)" size="large" effect="plain">
                {{ statusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="examScore" label="分数" width="80">
            <template #default="{ row }">
              {{ row.examScore != null ? row.examScore : '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="registerTime" label="报名时间" width="170" />
        </el-table>
      </template>

      <el-empty v-else description="暂无报名记录">
        <el-button type="primary" @click="$router.push('/student')">前往报名</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getMyRegistrations } from '@/api/student'

const loading = ref(true)
const list = ref<any[]>([])

const statusMap: Record<number, string> = { 0: '排队', 1: '待审核', 2: '通过', 3: '拒绝', 4: '递补' }
const statusTagMap: Record<number, string> = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger', 4: 'primary' }

function statusText(s: number) { return statusMap[s] || '未知' }
function statusTag(s: number): 'info' | 'warning' | 'success' | 'danger' | 'primary' {
  return (statusTagMap[s] || 'info') as any
}

onMounted(async () => {
  try {
    const res = await getMyRegistrations()
    list.value = Array.isArray(res) ? res : res.records || []
  } catch {
    list.value = []
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.result-page { max-width: 800px; margin: 0 auto; }
.page-header {
  text-align: center;
  padding: 32px 0 24px;
}
.page-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #0d9488;
  margin: 0 0 8px;
}
.subtitle { color: #999; font-size: 14px; margin: 0; }
.result-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 16px rgba(0,0,0,0.06);
  padding: 24px;
  min-height: 300px;
}
</style>
