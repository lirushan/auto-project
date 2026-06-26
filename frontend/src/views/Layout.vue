<template>
  <el-container class="layout">
    <!-- Floating decoration circles (matches LoginPage) -->
    <div class="bg-shapes">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
    </div>

    <el-aside width="220px">
      <div class="logo">
        <div class="logo-icon">A</div>
        <span class="logo-text">Auto Project</span>
      </div>
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
import { HomeFilled, UserFilled, Setting, Avatar, Menu, ArrowDown, Document, List } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import type { MenuVO } from '@/api/menu'
import request from '@/api/index'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const iconMap: Record<string, any> = { HomeFilled, UserFilled, Setting, Avatar, Menu, Document, List, User: UserFilled }

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
  router.push('/admin/login')
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
/* ============================================================
   Layout — Root (position: relative creates stacking context
   for floating shapes, exactly matching LoginPage approach)
   ============================================================ */
.layout {
  height: 100vh;
  position: relative;
  overflow: hidden;
}

/* ============================================================
   Floating decoration circles (matching LoginPage)
   ============================================================ */
.bg-shapes {
  position: absolute;
  inset: 0;
  pointer-events: none;
}
.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.12);
}
.shape-1 {
  width: 400px;
  height: 400px;
  bottom: -120px;
  right: -80px;
  animation: float 8s ease-in-out infinite;
}
.shape-2 {
  width: 250px;
  height: 250px;
  bottom: -60px;
  left: 200px;
  animation: float 10s ease-in-out infinite reverse;
}
.shape-3 {
  width: 180px;
  height: 180px;
  top: 45%;
  right: 6%;
  animation: float 6s ease-in-out infinite;
}
@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-30px) rotate(5deg);
  }
}

/* ============================================================
   Sidebar
   ============================================================ */
.el-aside {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 1;
  overflow: hidden;
}

/* Inner container — ensure it sits above floating shapes */
.layout > :deep(.el-container) {
  position: relative;
  z-index: 1;
}

/* Subtle top gradient accent line */
.el-aside::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(102, 126, 234, 0.4), transparent);
  z-index: 1;
}

/* ============================================================
   Logo area
   ============================================================ */
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 0 16px;
}
.logo-icon {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 800;
  color: #fff;
  flex-shrink: 0;
}
.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  letter-spacing: -0.3px;
  white-space: nowrap;
}

/* ============================================================
   Sidebar Menu (deep overrides into Element Plus)
   ============================================================ */
:deep(.el-menu) {
  background: transparent;
  border-right: none;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  color: #a8b2d1;
  font-size: 14px;
  font-family: 'Inter', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  margin: 2px 8px;
  border-radius: 10px;
  transition: all 0.25s ease;
  height: 44px;
  line-height: 44px;
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background: rgba(102, 126, 234, 0.12) !important;
  color: #fff;
}

:deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.25), rgba(118, 75, 162, 0.2)) !important;
  color: #fff;
  font-weight: 600;
}

/* Sub-menu icon color */
:deep(.el-sub-menu__icon-arrow) {
  color: #8892b0;
}

/* Sub-menu popup (collapsed or nested) */
:deep(.el-menu--inline) {
  background: rgba(0, 0, 0, 0.12);
  border-radius: 8px;
  margin: 0 8px;
}

/* ============================================================
   Top Header
   ============================================================ */
.el-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.12);
}
.header-title {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
}
.avatar-area {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 10px;
  border-radius: 8px;
  transition: background 0.25s;
}
.avatar-area:hover {
  background: rgba(255, 255, 255, 0.08);
}
.username {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
}

/* Avatar border glow */
:deep(.el-avatar) {
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.3);
}
</style>
