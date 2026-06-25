import request from './index'

export interface RoleVO {
  id: number
  roleName: string
  roleCode: string
  description: string
  status: number
  sortOrder: number
  createTime: string
  updateTime: string
}

export interface RoleCreateDTO {
  roleName: string
  roleCode: string
  description?: string
  sortOrder?: number
}

export interface RoleUpdateDTO {
  roleName: string
  roleCode: string
  description?: string
  sortOrder?: number
}

export interface RolePageResult {
  records: RoleVO[]
  total: number
}

export function getRoles(params: { page?: number; size?: number; keyword?: string }) {
  return request.get<RolePageResult>('/roles', { params })
}

export function createRole(data: RoleCreateDTO) {
  return request.post<RoleVO>('/roles', data)
}

export function updateRole(id: number, data: RoleUpdateDTO) {
  return request.put<RoleVO>(`/roles/${id}`, data)
}

export function deleteRole(id: number) {
  return request.delete(`/roles/${id}`)
}

export function bindRoleMenus(roleId: number, menuIds: number[]) {
  return request.put(`/roles/${roleId}/menus`, { menuIds })
}

export function getRoleMenus(roleId: number) {
  return request.get<number[]>(`/roles/${roleId}/menus`)
}
