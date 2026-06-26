import request from './index'

export function getStudentList(params: { page?: number; size?: number; keyword?: string }) {
  return request.get<any, { records: any[]; total: number; size: number; current: number }>('/students', { params })
}

export function getRegistrationList(params: { page?: number; size?: number; status?: number; courseId?: number }) {
  return request.get<any, { records: any[]; total: number; size: number; current: number }>('/registrations', { params })
}

export function getRegistrationStats() {
  return request.get<any, { details: any[]; queueCount: number; admittedCount: number; supplyCount: number }>('/registrations/stats')
}

export function getExamConfig() {
  return request.get<any, { quota: number; passScore: number; examDesc: string }>('/exam-config')
}

export function updateExamConfig(data: { quota: number; passScore: number }) {
  return request.put('/exam-config', data)
}
