import request from './index'
import type { MenuVO } from './menu'

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  userId: number
  username: string
  roles: string[]
  menus: MenuVO[]
}

export function login(data: LoginRequest) {
  return request.post<LoginResponse>('/auth/login', data)
}
