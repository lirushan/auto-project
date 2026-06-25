<template>
  <div class="menu-page">
    <div class="toolbar">
      <el-button type="primary" @click="openCreate">新增菜单</el-button>
      <el-button @click="expandAll">展开全部</el-button>
      <el-button @click="collapseAll">收起全部</el-button>
    </div>

    <el-table
      ref="tableRef"
      :data="menuTree"
      row-key="id"
      v-loading="loading"
      border
      default-expand-all
      :tree-props="{ children: 'children' }"
    >
      <el-table-column prop="menuName" label="菜单名称" min-width="180" />
      <el-table-column prop="icon" label="图标" width="80" />
      <el-table-column label="类型" width="80">
        <template #default="{ row }">
          <el-tag :type="menuTypeTag(row.menuType)" size="small">
            {{ menuTypeText(row.menuType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="path" label="路由路径" min-width="150" />
      <el-table-column prop="component" label="组件路径" min-width="150" show-overflow-tooltip />
      <el-table-column prop="permission" label="权限标识" min-width="150" show-overflow-tooltip />
      <el-table-column prop="sortOrder" label="排序" width="70" />
      <el-table-column label="状态" width="70">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑菜单' : '新增菜单'" width="550px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="上级菜单" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="parentOptions"
            :props="{ label: 'menuName', value: 'id', children: 'children' }"
            placeholder="无（顶级菜单）"
            clearable
            check-strictly
          />
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="form.menuType">
            <el-radio :value="0">目录</el-radio>
            <el-radio :value="1">菜单</el-radio>
            <el-radio :value="2">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-input v-model="form.icon" placeholder="请输入图标名称" />
        </el-form-item>
        <el-form-item label="路由路径" prop="path" v-if="form.menuType !== 2">
          <el-input v-model="form.path" placeholder="请输入路由路径" />
        </el-form-item>
        <el-form-item label="组件路径" prop="component" v-if="form.menuType === 1">
          <el-input v-model="form.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item label="权限标识" prop="permission">
          <el-input v-model="form.permission" placeholder="请输入权限标识" />
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
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, ElTable } from 'element-plus'
import { getMenus, createMenu, updateMenu, deleteMenu } from '@/api/menu'
import type { MenuVO, MenuCreateDTO } from '@/api/menu'

const menuTree = ref<MenuVO[]>([])
const loading = ref(false)
const tableRef = ref()

const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref<number | null>(null)
const submitting = ref(false)
const formRef = ref<FormInstance>()
const form = ref<MenuCreateDTO>({
  parentId: undefined,
  menuName: '',
  menuType: 0,
  path: '',
  component: '',
  icon: '',
  permission: '',
  sortOrder: 0,
})

const rules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }],
}

const parentOptions = ref<MenuVO[]>([])

function menuTypeText(type: number) {
  const map: Record<number, string> = { 0: '目录', 1: '菜单', 2: '按钮' }
  return map[type] || ''
}

function menuTypeTag(type: number) {
  const map: Record<number, string> = { 0: '', 1: 'success', 2: 'warning' }
  return map[type] || 'info'
}

function expandAll() {
  toggleExpand(true)
}

function collapseAll() {
  toggleExpand(false)
}

function toggleExpand(expanded: boolean) {
  const toggle = (nodes: MenuVO[]) => {
    nodes.forEach((node) => {
      tableRef.value?.toggleRowExpansion(node, expanded)
      if (node.children && node.children.length) {
        toggle(node.children)
      }
    })
  }
  toggle(menuTree.value)
}

function filterParents(nodes: MenuVO[]): MenuVO[] {
  return nodes
    .filter((n) => n.menuType !== 2)
    .map((n) => ({
      ...n,
      children: n.children ? filterParents(n.children) : [],
    }))
}

async function fetchMenus() {
  loading.value = true
  try {
    const res = await getMenus()
    menuTree.value = res
    parentOptions.value = [{ id: 0, menuName: '根目录', children: filterParents(res) } as MenuVO]
  } finally {
    loading.value = false
  }
}

function openCreate() {
  isEdit.value = false
  editId.value = null
  form.value = {
    parentId: undefined,
    menuName: '',
    menuType: 0,
    path: '',
    component: '',
    icon: '',
    permission: '',
    sortOrder: 0,
  }
  dialogVisible.value = true
}

function openEdit(menu: MenuVO) {
  isEdit.value = true
  editId.value = menu.id
  form.value = {
    parentId: menu.parentId || undefined,
    menuName: menu.menuName,
    menuType: menu.menuType,
    path: menu.path,
    component: menu.component,
    icon: menu.icon,
    permission: menu.permission,
    sortOrder: menu.sortOrder,
  }
  dialogVisible.value = true
}

async function submitForm() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value && editId.value) {
      await updateMenu(editId.value, form.value)
      ElMessage.success('更新成功')
    } else {
      await createMenu(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchMenus()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(menu: MenuVO) {
  const msg = menu.children && menu.children.length
    ? `菜单「${menu.menuName}」下存在子菜单，删除后子菜单也会一并删除，确定继续？`
    : `确定删除菜单「${menu.menuName}」吗？`
  await ElMessageBox.confirm(msg, '确认删除', { type: 'warning' })
  await deleteMenu(menu.id)
  ElMessage.success('删除成功')
  fetchMenus()
}

fetchMenus()
</script>

<style scoped>
.menu-page {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
</style>
