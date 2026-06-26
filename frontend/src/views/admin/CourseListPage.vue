<template>
  <div class="page">
    <div class="toolbar">
      <el-button type="primary" @click="openAdd">新增课程</el-button>
    </div>

    <el-table :data="list" v-loading="loading" stripe border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="courseName" label="课程名" />
      <el-table-column prop="timePeriod" label="时段" width="150" />
      <el-table-column prop="quota" label="名额" width="80" />
      <el-table-column prop="enrolledCount" label="已报名" width="80" />
      <el-table-column prop="remaining" label="剩余" width="80" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-switch
            :model-value="row.status === 1"
            @change="(val: boolean) => toggleStatus(row, val)"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑课程' : '新增课程'" width="560px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="form.courseName" />
        </el-form-item>
        <el-form-item label="课程描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="课程时段" prop="timePeriod">
          <el-input v-model="form.timePeriod" />
        </el-form-item>
        <el-form-item label="招生名额" prop="quota">
          <el-input-number v-model="form.quota" :min="1" />
        </el-form-item>
        <el-form-item label="分数线" prop="passScore">
          <el-input-number v-model="form.passScore" :min="0" />
        </el-form-item>
        <el-form-item label="考核说明" prop="examDesc">
          <el-input v-model="form.examDesc" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch :model-value="form.status === 1" @change="(val: boolean) => form.status = val ? 1 : 0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCourses, createCourse, updateCourse, deleteCourse } from '@/api/course'

const list = ref<any[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(0)
const formRef = ref()

const emptyForm = () => ({
  courseName: '',
  description: '',
  timePeriod: '',
  quota: 100,
  passScore: 60,
  examDesc: '',
  sortOrder: 0,
  status: 1,
})
const form = ref(emptyForm())

const rules = {
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
}

async function fetch() {
  loading.value = true
  try {
    const r = await getCourses({ page: page.value, size: size.value })
    list.value = r.records
    total.value = r.total
  } finally {
    loading.value = false
  }
}

function openAdd() {
  isEdit.value = false
  editId.value = 0
  form.value = emptyForm()
  dialogVisible.value = true
}

function openEdit(row: any) {
  isEdit.value = true
  editId.value = row.id
  form.value = {
    courseName: row.courseName || '',
    description: row.description || '',
    timePeriod: row.timePeriod || '',
    quota: row.quota || 100,
    passScore: row.passScore || 60,
    examDesc: row.examDesc || '',
    sortOrder: row.sortOrder || 0,
    status: row.status ?? 1,
  }
  dialogVisible.value = true
}

async function toggleStatus(row: any, val: boolean) {
  try {
    await updateCourse(row.id, { ...row, status: val ? 1 : 0, id: undefined })
    row.status = val ? 1 : 0
    ElMessage.success('状态已更新')
  } catch {
    // handled by interceptor
  }
}

async function remove(id: number) {
  await ElMessageBox.confirm('确定删除该课程？', '提示', { type: 'warning' })
  try {
    await deleteCourse(id)
    ElMessage.success('删除成功')
    fetch()
  } catch {
    // handled by interceptor
  }
}

async function submit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateCourse(editId.value, form.value)
      ElMessage.success('更新成功')
    } else {
      await createCourse(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetch()
  } finally {
    submitting.value = false
  }
}

fetch()
</script>

<style scoped>
.page { background: #fff; border-radius: 8px; padding: 20px; }
.toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
