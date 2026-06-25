import api from './index'

export interface User {
  id: number
  username: string
  email: string
  createTime: string
  updateTime: string
}

export interface UserPage {
  records: User[]
  total: number
  size: number
  current: number
}

export interface UserParams {
  page?: number
  size?: number
  keyword?: string
}

export interface UserForm {
  username: string
  email: string
}

export function getUsers(params: UserParams) {
  return api.get<any, UserPage>('/users', { params })
}

export function createUser(data: UserForm) {
  return api.post<any, User>('/users', data)
}

export function updateUser(id: number, data: UserForm) {
  return api.put<any, User>(`/users/${id}`, data)
}

export function deleteUser(id: number) {
  return api.delete(`/users/${id}`)
}

export interface UserRoleVO {
  id: number
  roleName: string
  roleCode: string
}

export function getUserRoles(userId: number) {
  return api.get<any, UserRoleVO[]>(`/users/${userId}/roles`)
}

export function assignUserRoles(userId: number, roleIds: number[]) {
  return api.put(`/users/${userId}/roles`, { roleIds })
}
