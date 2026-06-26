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
          <div class="brand-icon">S</div>
          <h1>暑托班报名</h1>
          <p>手机号验证码快捷登录</p>
        </div>
        <div class="features">
          <div class="feature"><span class="dot"></span> 在线报名</div>
          <div class="feature"><span class="dot"></span> 实时排名</div>
          <div class="feature"><span class="dot"></span> 考核结果查询</div>
        </div>
      </div>
      <div class="card-right">
        <h2>学生登录</h2>
        <p class="tip">输入手机号获取验证码</p>
        <el-form :model="form" :rules="rules" ref="formRef" @keyup.enter="tryLogin">
          <el-form-item prop="phone">
            <el-input
              v-model="form.phone"
              placeholder="请输入手机号"
              size="large"
              :prefix-icon="Phone"
              maxlength="11"
            />
          </el-form-item>
          <el-form-item prop="code">
            <div class="code-row">
              <el-input
                v-model="form.code"
                placeholder="验证码"
                size="large"
                :prefix-icon="Lock"
                maxlength="6"
                class="code-input"
              />
              <el-button
                :disabled="codeSending || countdown > 0"
                :loading="codeSending"
                @click="sendVerificationCode"
                class="code-btn"
              >
                {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
              </el-button>
            </div>
          </el-form-item>
          <el-alert title="开发模式: 验证码固定为 123456" type="info" :closable="false" show-icon style="margin-bottom: 12px" />
          <el-button
            type="primary"
            size="large"
            :loading="loading"
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { Phone, Lock } from '@element-plus/icons-vue'
import { sendCode, login } from '@/api/student'

const router = useRouter()
const formRef = ref<FormInstance>()
const form = ref({ phone: '', code: '' })
const loading = ref(false)

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' },
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
}

const countdown = ref(0)
const codeSending = ref(false)
let timer: ReturnType<typeof setInterval> | null = null

async function sendVerificationCode() {
  if (codeSending.value || countdown.value > 0) return
  const valid = await formRef.value?.validateField('phone').catch(() => false)
  if (!valid) return
  codeSending.value = true
  try {
    await sendCode(form.value.phone)
    form.value.code = '123456'  // 开发模式自动填充
    ElMessage.success('验证码已发送（123456）')
    countdown.value = 60
    timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        if (timer) clearInterval(timer)
        timer = null
      }
    }, 1000)
  } catch {
    // error handled by interceptor
  } finally {
    codeSending.value = false
  }
}

async function tryLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const data = await login(form.value.phone, form.value.code)
    localStorage.setItem('student_token', data.token)
    localStorage.setItem('student_info', JSON.stringify({ id: data.id, name: data.name, phone: data.phone }))
    ElMessage.success('登录成功')
    router.push('/student')
  } catch {
    // error handled by interceptor
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (localStorage.getItem('student_token')) router.push('/student')
})
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
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
  opacity: 0.12;
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
  box-shadow: 0 25px 60px rgba(0,0,0,0.15);
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
  background: linear-gradient(135deg, #0d9488 0%, #0f766e 50%, #115e59 100%);
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.brand { color: #fff; }
.brand-icon {
  width: 48px; height: 48px;
  background: linear-gradient(135deg, #43e97b, #38f9d7);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 800;
  margin-bottom: 24px;
  color: #0d9488;
}
.brand h1 {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 12px;
  letter-spacing: -0.5px;
}
.brand p {
  font-size: 14px;
  color: rgba(255,255,255,0.6);
  line-height: 1.6;
  margin: 0;
}
.features { margin-top: 40px; }
.feature {
  color: rgba(255,255,255,0.75);
  font-size: 13px;
  margin-bottom: 14px;
  display: flex;
  align-items: center;
  gap: 10px;
}
.dot {
  width: 6px; height: 6px;
  background: #43e97b;
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
.card-right h2 { font-size: 26px; font-weight: 700; color: #0d9488; margin: 0 0 6px; }
.tip { color: #999; font-size: 14px; margin-bottom: 36px; }
.code-row { display: flex; gap: 12px; width: 100%; }
.code-input { flex: 1; }
.code-btn { min-width: 120px; height: 40px; border-radius: 8px; }
.login-btn {
  width: 100%;
  height: 46px;
  font-size: 16px;
  letter-spacing: 4px;
  border-radius: 10px;
  background: linear-gradient(135deg, #43e97b, #38f9d7);
  border: none;
  color: #fff;
  margin-top: 4px;
}
.login-btn:hover { background: linear-gradient(135deg, #34d399, #2dd4bf); }
</style>
