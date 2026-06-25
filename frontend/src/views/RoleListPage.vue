<template>
  <div class="role-page">
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索角色名称/编码" clearable style="width: 240px" @keyup.enter="search" />
      <el-button type="primary" :icon="Search" @click="search">搜索</el-button>
      <el-button type="primary" @click="openCreate">新增角色</el-button>
    </div>

    <el-table :data="roles" v-loading="loading" stripe border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="roleName" label="角色名称" />
      <el-table-column prop="roleCode" label="角色编码" />
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="warning" @click="openMenuDialog(row)">菜单权限</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="page"
        :page-size="size"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="fetchRoles"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="请输入角色编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="menuDialogVisible" title="分配菜单权限" width="450px">
      <el-tree
        ref="menuTreeRef"
        :data="menuTree"
        node-key="id"
        show-checkbox
        default-expand-all
        :default-checked-keys="checkedMenuIds"
        :props="{ label: 'menuName', children: 'children' }"
      />
      <template #footer>
        <el-button @click="menuDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveMenuPermissions" :loading="savingMenus">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import type { FormInstance, ElTree } from 'element-plus'
import { getRoles, createRole, updateRole, deleteRole, bindRoleMenus, getRoleMenus } from '@/api/role'
import { getMenus } from '@/api/menu'
import type { RoleVO, RoleCreateDTO } from '@/api/role'
import type { MenuVO } from '@/api/menu'

const roles = ref<RoleVO[]>([])
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
const form = ref<RoleCreateDTO>({
  roleName: '',
  roleCode: '',
  description: '',
  sortOrder: 0,
})

const rules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
}

const menuDialogVisible = ref(false)
const currentRoleId = ref<number | null>(null)
const menuTree = ref<MenuVO[]>([])
const checkedMenuIds = ref<number[]>([])
const menuTreeRef = ref<InstanceType<typeof ElTree>>()
const savingMenus = ref(false)

function search() {
  page.value = 1
  fetchRoles()
}

async function fetchRoles() {
  loading.value = true
  try {
    const res = await getRoles({ page: page.value, size: size.value, keyword: keyword.value })
    roles.value = res.records
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function openCreate() {
  isEdit.value = false
  editId.value = null
  form.value = { roleName: '', roleCode: '', description: '', sortOrder: 0 }
  dialogVisible.value = true
}

function openEdit(role: RoleVO) {
  isEdit.value = true
  editId.value = role.id
  form.value = {
    roleName: role.roleName,
    roleCode: role.roleCode,
    description: role.description,
    sortOrder: role.sortOrder,
  }
  dialogVisible.value = true
}

async function submitForm() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value && editId.value) {
      await updateRole(editId.value, form.value)
      ElMessage.success('更新成功')
    } else {
      await createRole(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchRoles()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(role: RoleVO) {
  await ElMessageBox.confirm(`确定删除角色「${role.roleName}」吗？`, '确认删除', { type: 'warning' })
  await deleteRole(role.id)
  ElMessage.success('删除成功')
  fetchRoles()
}

async function openMenuDialog(role: RoleVO) {
  currentRoleId.value = role.id
  menuDialogVisible.value = true
  try {
    const [tree, ids] = await Promise.all([getMenus(), getRoleMenus(role.id)])
    menuTree.value = tree
    checkedMenuIds.value = ids
  } catch {
    menuDialogVisible.value = false
  }
}

async function saveMenuPermissions() {
  if (!currentRoleId.value) return
  savingMenus.value = true
  try {
    const checked = menuTreeRef.value?.getCheckedKeys() as number[]
    const halfChecked = menuTreeRef.value?.getHalfCheckedKeys() as number[]
    await bindRoleMenus(currentRoleId.value, [...checked, ...halfChecked])
    ElMessage.success('菜单权限保存成功')
    menuDialogVisible.value = false
  } finally {
    savingMenus.value = false
  }
}

fetchRoles()
</script>

<style scoped>
.role-page {
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
