<template>
  <div class="login-page">
    <div class="bg-shapes">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
    </div>
    <div class="login-card">
      <div class="card-left">
        <div class="brand">
          <div class="brand-icon">A</div>
          <h1>Auto Project</h1>
          <p>AI-driven full-stack autonomous development pipeline</p>
        </div>
        <div class="features">
          <div class="feature">
            <span class="dot"></span> GitHub Issue 驱动
          </div>
          <div class="feature">
            <span class="dot"></span> 前后端并行 Agent
          </div>
          <div class="feature">
            <span class="dot"></span> Docker 一键部署
          </div>
        </div>
      </div>
      <div class="card-right">
        <h2>欢迎回来</h2>
        <p class="tip">登录以管理你的自动化流水线</p>
        <el-form :model="form" :rules="rules" ref="formRef" @keyup.enter="tryLogin">
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="用户名"
              size="large"
              :prefix-icon="User"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="密码"
              size="large"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          <!-- 滑动验证 -->
          <el-form-item prop="captcha" class="captcha-item">
            <div class="slider-captcha" :class="{ verified: captchaVerified }">
              <div class="slider-track">
                <div class="slider-fill" :style="{ width: sliderPercent + '%' }"></div>
                <div
                  class="slider-thumb"
                  :style="{ left: sliderLeft + 'px' }"
                  @mousedown="onDragStart"
                  @touchstart.prevent="onDragStart"
                >
                  <el-icon :size="20" v-if="!captchaVerified"><ArrowRightBold /></el-icon>
                  <el-icon :size="20" v-else><Check /></el-icon>
                </div>
                <span class="slider-text" v-if="!captchaVerified">按住滑块，拖动到最右边</span>
                <span class="slider-text" v-else>验证通过</span>
              </div>
            </div>
          </el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            :disabled="!captchaVerified"
            @click="tryLogin"
            class="login-btn"
          >
            登 录
          </el-button>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { User, Lock, ArrowRightBold, Check } from '@element-plus/icons-vue'
import { login } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref<FormInstance>()
const form = ref({ username: 'lirushan', password: '123456', captcha: true })
const loading = ref(false)

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

// Slider captcha
const captchaVerified = ref(false)
const sliderLeft = ref(0)
const sliderPercent = ref(0)
const maxSlide = 280
let dragging = false

function onDragStart(e: MouseEvent | TouchEvent) {
  if (captchaVerified.value) return
  dragging = true
  const startX = 'touches' in e ? e.touches[0].clientX : e.clientX
  const startLeft = sliderLeft.value

  const onMove = (ev: MouseEvent | TouchEvent) => {
    if (!dragging) return
    const clientX = 'touches' in ev ? (ev as TouchEvent).touches[0].clientX : (ev as MouseEvent).clientX
    const delta = clientX - startX
    const newLeft = Math.max(0, Math.min(maxSlide, startLeft + delta))
    sliderLeft.value = newLeft
    sliderPercent.value = Math.round((newLeft / maxSlide) * 100)
  }

  const onUp = () => {
    dragging = false
    document.removeEventListener('mousemove', onMove)
    document.removeEventListener('mouseup', onUp)
    document.removeEventListener('touchmove', onMove)
    document.removeEventListener('touchend', onUp)
    if (sliderLeft.value >= maxSlide - 5) {
      captchaVerified.value = true
      sliderLeft.value = maxSlide
      sliderPercent.value = 100
    } else {
      sliderLeft.value = 0
      sliderPercent.value = 0
    }
  }

  document.addEventListener('mousemove', onMove)
  document.addEventListener('mouseup', onUp)
  document.addEventListener('touchmove', onMove)
  document.addEventListener('touchend', onUp)
}

async function tryLogin() {
  if (!captchaVerified.value) {
    ElMessage.warning('请先完成滑动验证')
    return
  }
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const data = await login(form.value)
    userStore.setLogin(data)
    ElMessage.success('登录成功')
    router.push('/')
  } catch {
    // reset captcha
    captchaVerified.value = false
    sliderLeft.value = 0
    sliderPercent.value = 0
  } finally {
    loading.value = false
  }
}

onMounted(() => { if (localStorage.getItem('token')) router.push('/') })
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}
.bg-shapes {
  position: absolute;
  inset: 0;
  pointer-events: none;
}
.shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.15;
}
.shape-1 {
  width: 500px; height: 500px;
  background: #fff;
  top: -150px; right: -100px;
  animation: float 8s ease-in-out infinite;
}
.shape-2 {
  width: 300px; height: 300px;
  background: #fff;
  bottom: -80px; left: -60px;
  animation: float 10s ease-in-out infinite reverse;
}
.shape-3 {
  width: 200px; height: 200px;
  background: rgba(255,255,255,0.3);
  top: 50%; left: 10%;
  animation: float 6s ease-in-out infinite;
}
@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-30px) rotate(5deg); }
}
.login-card {
  display: flex;
  width: 900px;
  background: rgba(255,255,255,0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  box-shadow: 0 25px 60px rgba(0,0,0,0.2);
  overflow: hidden;
  z-index: 1;
  animation: slideUp 0.6s ease-out;
}
@keyframes slideUp {
  from { opacity: 0; transform: translateY(40px); }
  to { opacity: 1; transform: translateY(0); }
}
.card-left {
  width: 420px;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.brand { color: #fff; }
.brand-icon {
  width: 48px; height: 48px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 800;
  margin-bottom: 24px;
}
.brand h1 {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 12px;
  letter-spacing: -0.5px;
}
.brand p {
  font-size: 14px;
  color: rgba(255,255,255,0.55);
  line-height: 1.6;
  margin: 0;
}
.features { margin-top: 40px; }
.feature {
  color: rgba(255,255,255,0.7);
  font-size: 13px;
  margin-bottom: 14px;
  display: flex;
  align-items: center;
  gap: 10px;
}
.dot {
  width: 6px; height: 6px;
  background: #667eea;
  border-radius: 50%;
  display: inline-block;
}
.card-right {
  flex: 1;
  padding: 60px 50px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.card-right h2 { font-size: 26px; font-weight: 700; color: #1a1a2e; margin: 0 0 6px; }
.tip { color: #999; font-size: 14px; margin-bottom: 36px; }
.login-btn {
  width: 100%;
  height: 46px;
  font-size: 16px;
  letter-spacing: 4px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  margin-top: 4px;
}

/* Slider captcha */
.slider-captcha {
  width: 100%;
}
.slider-track {
  position: relative;
  width: 100%;
  height: 44px;
  background: #f0f2f5;
  border-radius: 22px;
  overflow: hidden;
  user-select: none;
}
.slider-fill {
  position: absolute;
  height: 100%;
  background: linear-gradient(90deg, #e8f5e9, #c8e6c9);
  border-radius: 22px 0 0 22px;
  transition: width 0.1s;
}
.verified .slider-fill { background: linear-gradient(90deg, #e8f5e9, #a5d6a7); }
.slider-thumb {
  position: absolute;
  top: 2px;
  width: 40px; height: 40px;
  background: #fff;
  border-radius: 50%;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: grab;
  color: #667eea;
  z-index: 2;
  transition: none;
}
.verified .slider-thumb { background: #4caf50; color: #fff; cursor: default; }
.slider-text {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  color: #999;
  pointer-events: none;
}
.captcha-item :deep(.el-form-item__content) { line-height: 1; }
</style>
