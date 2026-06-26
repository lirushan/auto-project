<template>
  <div class="enroll-page">
    <el-page-header @back="$router.push('/student')" title="返回">
      <template #content><span class="page-title">课程报名确认</span></template>
    </el-page-header>

    <div class="content" v-loading="loading">
      <div v-if="!courseId" class="empty-state">
        <el-empty description="请从课程列表选择课程报名">
          <el-button type="primary" @click="$router.push('/student')">返回课程列表</el-button>
        </el-empty>
      </div>

      <template v-else>
        <div class="course-info">
          <el-descriptions title="课程信息" :column="2" border>
            <el-descriptions-item label="课程名称">{{ course.courseName }}</el-descriptions-item>
            <el-descriptions-item label="课程时段">{{ course.timePeriod || '-' }}</el-descriptions-item>
            <el-descriptions-item label="招生名额">{{ course.quota }} 人</el-descriptions-item>
            <el-descriptions-item label="剩余名额">{{ remaining }} 人</el-descriptions-item>
            <el-descriptions-item label="描述" :span="2">{{ course.description || '暂无' }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="action-area">
          <el-alert
            v-if="enrolled"
            type="success"
            :closable="false"
            show-icon
            title="报名成功"
            :description="`您的报名序号为 ${rankNum}，请关注后续考核通知。`"
          />

          <div v-else class="confirm-section">
            <el-alert
              v-if="remaining <= 0"
              type="warning"
              :closable="false"
              show-icon
              title="名额已满"
              description="抱歉，该课程名额已满，无法继续报名。"
            />
            <template v-else>
              <p class="confirm-tip">确认以上课程信息无误后，点击下方按钮完成报名。</p>
              <el-button
                type="primary"
                size="large"
                :loading="submitting"
                @click="confirmEnroll"
                class="enroll-btn"
              >
                确认报名
              </el-button>
            </template>
          </div>
        </div>

        <el-dialog v-model="resultDialog" title="报名成功" width="400px" center>
          <div class="result-content">
            <el-result icon="success" title="报名成功">
              <template #sub-title>
                您的报名序号为
                <strong style="color:#0d9488;font-size:24px">{{ resultRank }}</strong>
                <div v-if="resultMessage" style="margin-top:8px;color:#666">{{ resultMessage }}</div>
              </template>
            </el-result>
          </div>
          <template #footer>
            <el-button type="primary" @click="$router.push('/student')">返回首页</el-button>
            <el-button @click="$router.push('/student/result')">查看报名记录</el-button>
          </template>
        </el-dialog>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getCourses, enroll } from '@/api/student'
import { getMyRegistrations } from '@/api/student'

const route = useRoute()
const courseId = ref(Number(route.query.courseId) || 0)
const loading = ref(true)
const submitting = ref(false)
const enrolled = ref(false)
const rankNum = ref(0)
const resultRank = ref(0)
const resultMessage = ref('')
const resultDialog = ref(false)
const course = ref({ courseName: '', timePeriod: '', quota: 100, description: '', remaining: 0 })

const remaining = computed(() => Math.max(0, (course.value.remaining ?? course.value.quota) - (enrolled.value ? 1 : 0)))

onMounted(async () => {
  if (!courseId.value) {
    loading.value = false
    return
  }

  try {
    const courses = await getCourses()
    const list = Array.isArray(courses) ? courses : courses.records || []
    const found = list.find((c: any) => c.id === courseId.value)
    if (found) course.value = found
  } catch { /* use defaults */ }

  try {
    const regs = await getMyRegistrations()
    const list = Array.isArray(regs) ? regs : regs.records || []
    const r = list.find((r: any) => r.courseId === courseId.value)
    if (r) {
      enrolled.value = true
      rankNum.value = r.rankNum
    }
  } catch {
    // not enrolled
  }

  loading.value = false
})

async function confirmEnroll() {
  submitting.value = true
  try {
    const data = await enroll(courseId.value)
    resultRank.value = data.rankNum
    resultMessage.value = data.message || ''
    enrolled.value = true
    rankNum.value = data.rankNum
    resultDialog.value = true
  } catch {
    // error handled by interceptor
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.enroll-page { max-width: 800px; margin: 0 auto; }
.page-title { font-size: 18px; font-weight: 600; color: #0d9488; }
.content { margin-top: 24px; }
.empty-state { padding: 60px 0; }
.course-info { margin-bottom: 32px; }
.action-area {
  background: #fff;
  border-radius: 12px;
  padding: 32px;
  text-align: center;
  box-shadow: 0 2px 16px rgba(0,0,0,0.06);
}
.confirm-tip { color: #666; font-size: 15px; margin-bottom: 24px; }
.enroll-btn {
  min-width: 200px;
  height: 48px;
  font-size: 16px;
  border-radius: 10px;
  background: linear-gradient(135deg, #43e97b, #38f9d7);
  border: none;
  color: #fff;
}
.result-content { padding: 20px 0; }
</style>
