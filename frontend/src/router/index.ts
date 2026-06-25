import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/views/Layout.vue'
import LoginPage from '@/views/LoginPage.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginPage,
    },
    {
      path: '/',
      component: Layout,
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('@/views/HomePage.vue'),
        },
        {
          path: 'users',
          name: 'users',
          component: () => import('@/views/UserListPage.vue'),
        },
        {
          path: 'roles',
          name: 'roles',
          component: () => import('@/views/RoleListPage.vue'),
        },
        {
          path: 'menus',
          name: 'menus',
          component: () => import('@/views/MenuListPage.vue'),
        },
      ],
    },
  ],
})

router.beforeEach((to) => {
  if (to.path !== '/login' && !localStorage.getItem('token')) {
    return '/login'
  }
})

export default router
