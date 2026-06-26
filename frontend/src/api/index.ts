import axios from 'axios'
import { ElMessage } from 'element-plus'

const api = axios.create({
  baseURL: '/api/v1',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.request.use((config) => {
  const adminToken = localStorage.getItem('token')
  const studentToken = localStorage.getItem('student_token')
  if (studentToken && window.location.pathname.startsWith('/student')) {
    config.headers.Authorization = `Bearer ${studentToken}`
  } else if (adminToken) {
    config.headers.Authorization = `Bearer ${adminToken}`
  }
  return config
})

api.interceptors.response.use(
  (res) => {
    const { code, message, data } = res.data
    if (code !== 200) {
      ElMessage.error(message || '请求失败')
      return Promise.reject(new Error(message))
    }
    return data
  },
  (error) => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default api
