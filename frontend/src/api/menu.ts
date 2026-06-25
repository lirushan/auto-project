import request from './index'

export interface MenuVO {
  id: number
  parentId: number
  menuName: string
  menuType: number
  path: string
  component: string
  icon: string
  permission: string
  sortOrder: number
  status: number
  visible: number
  createTime: string
  updateTime: string
  children?: MenuVO[]
}

export interface MenuCreateDTO {
  parentId?: number
  menuName: string
  menuType: number
  path?: string
  component?: string
  icon?: string
  permission?: string
  sortOrder?: number
}

export interface MenuUpdateDTO {
  parentId?: number
  menuName: string
  menuType: number
  path?: string
  component?: string
  icon?: string
  permission?: string
  sortOrder?: number
}

export function getMenus() {
  return request.get<MenuVO[]>('/menus')
}

export function createMenu(data: MenuCreateDTO) {
  return request.post<MenuVO>('/menus', data)
}

export function updateMenu(id: number, data: MenuUpdateDTO) {
  return request.put<MenuVO>(`/menus/${id}`, data)
}

export function deleteMenu(id: number) {
  return request.delete(`/menus/${id}`)
}
