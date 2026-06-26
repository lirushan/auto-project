<template>
  <div class="page">
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索姓名/身份证" clearable style="width:240px" @keyup.enter="search" />
    </div>
    <el-table :data="list" v-loading="loading" stripe border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="student_name" label="姓名" />
      <el-table-column prop="id_card" label="身份证号" width="180" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="grade" label="年级" width="100" />
      <el-table-column prop="school" label="学校" />
      <el-table-column prop="create_time" label="注册时间" width="160" />
    </el-table>
    <div class="pagination">
      <el-pagination v-model:current-page="page" :page-size="size" :total="total" layout="total,prev,pager,next" @current-change="fetch" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { getStudentList } from '@/api/biz'
const list = ref<any[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const keyword = ref('')
const loading = ref(false)
async function fetch() { loading.value = true; try { const r = await getStudentList({ page: page.value, size: size.value, keyword: keyword.value }); list.value = r.records; total.value = r.total } finally { loading.value = false } }
function search() { page.value = 1; fetch() }
fetch()
</script>

<style scoped>
.page { background: #fff; border-radius: 8px; padding: 20px; }
.toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
