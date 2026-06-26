import request from './index'

export function sendCode(phone: string) {
  return request.post('/student/send-code', { phone })
}

export function login(phone: string, code: string) {
  return request.post('/student/login', { phone, code })
}

export function getProfile() {
  return request.get('/student/profile')
}

export function enroll(courseId: number) {
  return request.post('/student/enroll', { courseId })
}

export function getCourses() {
  return request.get('/student/courses')
}

export function getMyRegistrations() {
  return request.get('/student/my-registrations')
}
