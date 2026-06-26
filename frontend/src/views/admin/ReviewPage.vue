<template>
  <div class="page">
    <div class="toolbar">
      <el-select
        v-model="selectedCourse"
        placeholder="请选择课程"
        @change="onCourseChange"
        style="width: 300px"
        clearable
      >
        <el-option
          v-for="c in courses"
          :key="c.id"
          :label="c.courseName"
          :value="c.id"
        />
      </el-select>
    </div>

    <template v-if="selectedCourse">
      <el-table
        :data="list"
        v-loading="loading"
        stripe
        border
        @selection-change="onSelectionChange"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="studentName" label="姓名" />
        <el-table-column prop="rankNum" label="排名" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">
              {{ statusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="registerTime" label="报名时间" width="170" />
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          :page-size="size"
          :total="total"
          layout="total,prev,pager,next"
          @current-change="fetch"
        />
      </div>

      <div class="actions">
        <el-button
          type="success"
          :disabled="selectedIds.length === 0"
          @click="batchApprove"
        >
          批量通过 ({{ selectedIds.length }})
        </el-button>
        <el-button
          type="danger"
          :disabled="selectedIds.length === 0"
          @click="batchReject"
        >
          批量拒绝 ({{ selectedIds.length }})
        </el-button>
      </div>
    </template>

    <el-empty v-else description="请先选择课程" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCourses, getCourseRegistrations, batchReview } from '@/api/course'

const courses = ref<any[]>([])
const selectedCourse = ref<number | null>(null)
const list = ref<any[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)
const selectedIds = ref<number[]>([])

const statusMap: Record<number, string> = { 0: '排队', 1: '待审核', 2: '通过', 3: '拒绝', 4: '递补' }
const statusTagMap: Record<number, string> = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger', 4: 'primary' }

function statusText(s: number) { return statusMap[s] || '未知' }
function statusTagType(s: number): 'info' | 'warning' | 'success' | 'danger' | 'primary' {
  return (statusTagMap[s] || 'info') as any
}

async function loadCourses() {
  const r = await getCourses({ status: 1, size: 999 })
  courses.value = r.records || []
}

function onCourseChange() {
  page.value = 1
  selectedIds.value = []
  fetch()
}

function onSelectionChange(rows: any[]) {
  selectedIds.value = rows.map((r) => r.id)
}

async function fetch() {
  if (!selectedCourse.value) return
  loading.value = true
  try {
    const r = await getCourseRegistrations(selectedCourse.value, {
      page: page.value,
      size: size.value,
    })
    list.value = r.records
    total.value = r.total
  } finally {
    loading.value = false
  }
}

async function batchApprove() {
  await doBatch('approve', '通过')
}

async function batchReject() {
  await doBatch('reject', '拒绝')
}

async function doBatch(action: string, label: string) {
  try {
    await ElMessageBox.confirm(`确定${label}选中的 ${selectedIds.value.length} 条记录？`, '提示', {
      type: 'warning',
    })
  } catch {
    return
  }
  try {
    await batchReview(selectedCourse.value!, { ids: selectedIds.value, action })
    ElMessage.success(`${label}成功`)
    selectedIds.value = []
    fetch()
  } catch {
    // handled by interceptor
  }
}

loadCourses()
</script>

<style scoped>
.page { background: #fff; border-radius: 8px; padding: 20px; }
.toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
.actions { margin-top: 16px; display: flex; gap: 12px; }
</style>
