import request from './index'

export function getCourses(params: { page?: number; size?: number; status?: number }) {
  return request.get('/courses', { params })
}

export function createCourse(data: any) {
  return request.post('/courses', data)
}

export function updateCourse(id: number, data: any) {
  return request.put(`/courses/${id}`, data)
}

export function deleteCourse(id: number) {
  return request.delete(`/courses/${id}`)
}

export function getCourseRegistrations(id: number, params: { page?: number; size?: number; status?: number }) {
  return request.get(`/courses/${id}/registrations`, { params })
}

export function batchReview(courseId: number, data: { ids: number[]; action: string }) {
  return request.post(`/courses/${courseId}/review`, data)
}
