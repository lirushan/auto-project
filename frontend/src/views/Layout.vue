<template>
  <el-container class="layout">
    <el-aside width="220px">
      <div class="logo">Auto Project</div>
      <el-menu :default-active="route.path" router>
        <template v-for="item in menuTree" :key="item.id">
          <el-sub-menu v-if="item.menuType === 0 && item.children?.length" :index="String(item.id)">
            <template #title>
              <el-icon v-if="item.icon"><component :is="iconMap[item.icon]" /></el-icon>
              <span>{{ item.menuName }}</span>
            </template>
            <el-menu-item v-for="child in item.children" :key="child.id" :index="child.path">
              <el-icon v-if="child.icon"><component :is="iconMap[child.icon]" /></el-icon>
              <span>{{ child.menuName }}</span>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="item.path">
            <el-icon v-if="item.icon"><component :is="iconMap[item.icon]" /></el-icon>
            <span>{{ item.menuName }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <span class="header-title">AI 全栈开发流水线</span>
        <el-dropdown trigger="click" @command="handleCommand">
          <span class="avatar-area">
            <el-avatar :size="32" icon="UserFilled" />
            <span class="username">{{ userStore.username }}</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="password">修改密码</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="pwdDialogVisible" title="修改密码" width="400px">
      <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" label-width="80px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword" :loading="pwdLoading">确定</el-button>
      </template>
    </el-dialog>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { HomeFilled, UserFilled, Setting, Avatar, Menu, ArrowDown } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import type { MenuVO } from '@/api/menu'
import request from '@/api/index'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const iconMap: Record<string, any> = { HomeFilled, UserFilled, Setting, Avatar, Menu }

function buildTree(menus: MenuVO[]): MenuVO[] {
  const map = new Map<number, MenuVO>()
  const roots: MenuVO[] = []
  menus.forEach(m => map.set(m.id, { ...m, children: [] }))
  menus.forEach(m => {
    const node = map.get(m.id)!
    if (m.parentId === 0) {
      roots.push(node)
    } else {
      const parent = map.get(m.parentId)
      if (parent) {
        if (!parent.children) parent.children = []
        parent.children.push(node)
      }
    }
  })
  return roots
}

const menuTree = computed(() => buildTree(userStore.menus))

function handleCommand(cmd: string) {
  if (cmd === 'logout') handleLogout()
  else if (cmd === 'password') openPasswordDialog()
}

function handleLogout() {
  userStore.logout()
  router.push('/login')
}

// Password change
const pwdDialogVisible = ref(false)
const pwdLoading = ref(false)
const pwdFormRef = ref<FormInstance>()
const pwdForm = ref({ oldPassword: '', newPassword: '' })
const pwdRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' },
  ],
}

function openPasswordDialog() {
  pwdForm.value = { oldPassword: '', newPassword: '' }
  pwdDialogVisible.value = true
}

async function handleChangePassword() {
  const valid = await pwdFormRef.value?.validate().catch(() => false)
  if (!valid) return
  pwdLoading.value = true
  try {
    await request.put(`/users/${userStore.userId}/password`, {
      oldPassword: pwdForm.value.oldPassword,
      newPassword: pwdForm.value.newPassword,
    })
    ElMessage.success('密码修改成功，请重新登录')
    pwdDialogVisible.value = false
    handleLogout()
  } catch {
    // error handled by interceptor
  } finally {
    pwdLoading.value = false
  }
}
</script>

<style scoped>
.layout { height: 100vh; }
.el-aside { background: #304156; display: flex; flex-direction: column; }
.logo { height: 60px; line-height: 60px; text-align: center; font-size: 18px; font-weight: 700; color: #fff; }
.el-menu { border-right: none; flex: 1; }
.el-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e4e7ed;
  background: #fff;
}
.header-title { font-size: 14px; color: var(--el-text-color-secondary); }
.avatar-area {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
}
.avatar-area:hover { background: #f5f7fa; }
.username { font-size: 14px; }
</style>
