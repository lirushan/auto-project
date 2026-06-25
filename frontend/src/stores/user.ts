import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { MenuVO } from '@/api/menu'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userId = ref(Number(localStorage.getItem('userId')) || 0)
  const username = ref(localStorage.getItem('username') || '')
  const roles = ref<string[]>(JSON.parse(localStorage.getItem('roles') || '[]'))
  const menus = ref<MenuVO[]>(JSON.parse(localStorage.getItem('menus') || '[]'))

  function setLogin(data: { token: string; userId: number; username: string; roles: string[]; menus: MenuVO[] }) {
    token.value = data.token
    userId.value = data.userId
    username.value = data.username
    roles.value = data.roles
    menus.value = data.menus
    localStorage.setItem('token', data.token)
    localStorage.setItem('userId', String(data.userId))
    localStorage.setItem('username', data.username)
    localStorage.setItem('roles', JSON.stringify(data.roles))
    localStorage.setItem('menus', JSON.stringify(data.menus))
  }

  function logout() {
    token.value = ''
    userId.value = 0
    username.value = ''
    roles.value = []
    menus.value = []
    localStorage.clear()
  }

  return { token, userId, username, roles, menus, setLogin, logout }
})
