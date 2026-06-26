<template>
  <div class="user-page">
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索用户名" clearable style="width: 240px" @keyup.enter="search" />
      <el-button type="primary" @click="openCreate">新建用户</el-button>
    </div>

    <el-table :data="users" v-loading="loading" stripe border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-dropdown @command="(cmd: string) => handleCommand(cmd, row)">
            <el-button size="small" type="primary" plain>操作</el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="edit">编辑</el-dropdown-item>
                <el-dropdown-item command="role">分配角色</el-dropdown-item>
                <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="page"
        :page-size="size"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="fetchUsers"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新建用户'" width="480px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="roleDialogVisible" title="分配角色" width="480px">
      <el-checkbox-group v-model="assignedRoleIds">
        <div v-for="role in allRoles" :key="role.id" style="margin-bottom: 8px">
          <el-checkbox :value="role.id">{{ role.roleName }}（{{ role.roleCode }}）</el-checkbox>
        </div>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRoles" :loading="savingRoles">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getUsers, createUser, updateUser, deleteUser, getUserRoles, assignUserRoles } from '@/api/user'
import { getRoles } from '@/api/role'
import type { User, UserForm } from '@/api/user'
import type { RoleVO } from '@/api/role'

const users = ref<User[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const keyword = ref('')
const loading = ref(false)

const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref<number | null>(null)
const submitting = ref(false)
const formRef = ref<FormInstance>()
const form = ref<UserForm>({ username: '', email: '' })

const roleDialogVisible = ref(false)
const currentUserId = ref<number | null>(null)
const allRoles = ref<RoleVO[]>([])
const assignedRoleIds = ref<number[]>([])
const savingRoles = ref(false)

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
  ],
}

function search() {
  page.value = 1
  fetchUsers()
}

async function fetchUsers() {
  loading.value = true
  try {
    const res = await getUsers({ page: page.value, size: size.value, keyword: keyword.value })
    users.value = res.records
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function openCreate() {
  isEdit.value = false
  editId.value = null
  form.value = { username: '', email: '' }
  dialogVisible.value = true
}

function openEdit(user: User) {
  isEdit.value = true
  editId.value = user.id
  form.value = { username: user.username, email: user.email }
  dialogVisible.value = true
}

async function submitForm() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value && editId.value) {
      await updateUser(editId.value, form.value)
      ElMessage.success('更新成功')
    } else {
      await createUser(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchUsers()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(user: User) {
  await ElMessageBox.confirm(`确定删除用户「${user.username}」吗？`, '确认删除', { type: 'warning' })
  await deleteUser(user.id)
  ElMessage.success('删除成功')
  fetchUsers()
}

function handleCommand(cmd: string, row: User) {
  switch (cmd) {
    case 'edit': openEdit(row); break
    case 'role': openRoleDialog(row); break
    case 'delete': handleDelete(row); break
  }
}

async function openRoleDialog(user: User) {
  currentUserId.value = user.id
  roleDialogVisible.value = true
  try {
    const [rolesRes, userRoles] = await Promise.all([
      getRoles({ page: 1, size: 999 }),
      getUserRoles(user.id),
    ])
    allRoles.value = rolesRes.records
    assignedRoleIds.value = userRoles.map((r) => r.id)
  } catch {
    roleDialogVisible.value = false
  }
}

async function saveRoles() {
  if (!currentUserId.value) return
  savingRoles.value = true
  try {
    await assignUserRoles(currentUserId.value, assignedRoleIds.value)
    ElMessage.success('角色分配成功')
    roleDialogVisible.value = false
  } finally {
    savingRoles.value = false
  }
}

fetchUsers()
</script>

<style scoped>
.user-page {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
