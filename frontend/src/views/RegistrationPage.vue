<template>
  <div class="page">
    <div class="stats">
      <el-card><template #header>队列中</template><h2>{{ stats.queueCount }}</h2></el-card>
      <el-card><template #header>考核通过</template><h2>{{ stats.admittedCount }}</h2></el-card>
      <el-card><template #header>递补录取</template><h2>{{ stats.supplyCount }}</h2></el-card>
    </div>
    <div class="toolbar">
      <el-select v-model="statusFilter" placeholder="状态筛选" clearable @change="fetch" style="width:160px">
        <el-option label="全部" :value="undefined" />
        <el-option label="待考核" :value="1" />
        <el-option label="通过" :value="2" />
        <el-option label="未通过" :value="3" />
        <el-option label="递补" :value="4" />
      </el-select>
    </div>
    <el-table :data="list" v-loading="loading" stripe border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="studentName" label="学生" />
      <el-table-column prop="rankNum" label="报名序号" width="90" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }"><el-tag :type="statusTag(row.status)">{{ statusText(row.status) }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="examScore" label="考核分数" width="90" />
      <el-table-column prop="registerTime" label="报名时间" width="180" />
      <el-table-column prop="examTime" label="考核时间" width="180" />
    </el-table>
    <div class="pagination">
      <el-pagination v-model:current-page="page" :page-size="size" :total="total" layout="total,prev,pager,next" @current-change="fetch" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { getRegistrationList, getRegistrationStats } from '@/api/biz'
const list = ref<any[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const statusFilter = ref<number | undefined>()
const loading = ref(false)
const stats = ref({ queueCount: 0, admittedCount: 0, supplyCount: 0 })
async function fetch() { loading.value = true; try { const r = await getRegistrationList({ page: page.value, size: size.value, status: statusFilter.value }); list.value = r.records; total.value = r.total; const s = await getRegistrationStats(); stats.value = { queueCount: s.queueCount, admittedCount: s.admittedCount, supplyCount: s.supplyCount } } finally { loading.value = false } }
function statusText(s: number) { const m: Record<number,string> = {0:'排队',1:'待考核',2:'通过',3:'未通过',4:'递补'}; return m[s] || s }
function statusTag(s: number) { const m: Record<number,string> = {0:'info',1:'warning',2:'success',3:'danger',4:''}; return m[s] || 'info' }
fetch()
</script>

<style scoped>
.page { background: #fff; border-radius: 8px; padding: 20px; }
.stats { display: flex; gap: 16px; margin-bottom: 16px; }
.stats .el-card { flex: 1; text-align: center; }
.stats h2 { margin: 0; font-size: 28px; color: var(--el-color-primary); }
.toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
