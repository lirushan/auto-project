import axios from 'axios'

const api = axios.create({
  baseURL: '/api/v1',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.response.use(
  (res) => {
    const { code, message, data } = res.data
    if (code !== 200) {
      return Promise.reject(new Error(message || 'Request failed'))
    }
    return data
  },
  (error) => {
    return Promise.reject(error)
  }
)

export default api
