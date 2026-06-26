import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // === 学生端 ===
    {
      path: '/student/login',
      name: 'student-login',
      component: () => import('@/views/student/StudentLoginPage.vue'),
    },
    {
      path: '/student',
      component: () => import('@/views/student/StudentLayout.vue'),
      children: [
        { path: '', name: 'student-home', component: () => import('@/views/student/StudentHomePage.vue') },
        { path: 'enroll', name: 'student-enroll', component: () => import('@/views/student/StudentEnrollPage.vue') },
        { path: 'result', name: 'student-result', component: () => import('@/views/student/StudentResultPage.vue') },
      ],
    },
    // === 管理端 ===
    {
      path: '/admin/login',
      name: 'admin-login',
      component: () => import('@/views/LoginPage.vue'),
    },
    {
      path: '/admin',
      component: () => import('@/views/Layout.vue'),
      children: [
        { path: '', name: 'admin-home', redirect: '/admin/users' },
        { path: 'users', name: 'users', component: () => import('@/views/UserListPage.vue') },
        { path: 'roles', name: 'roles', component: () => import('@/views/RoleListPage.vue') },
        { path: 'menus', name: 'menus', component: () => import('@/views/MenuListPage.vue') },
        { path: 'students', name: 'students', component: () => import('@/views/StudentListPage.vue') },
        { path: 'registrations', name: 'registrations', component: () => import('@/views/RegistrationPage.vue') },
        { path: 'courses', name: 'courses', component: () => import('@/views/admin/CourseListPage.vue') },
        { path: 'review', name: 'review', component: () => import('@/views/admin/ReviewPage.vue') },
      ],
    },
    { path: '/', redirect: () => {
      if (localStorage.getItem('token')) return '/admin'
      if (localStorage.getItem('student_token')) return '/student'
      return '/student/login'
    }},
  ],
})

router.beforeEach((to) => {
  if (to.path.startsWith('/admin') && to.path !== '/admin/login' && !localStorage.getItem('token'))
    return '/admin/login'
  if (to.path.startsWith('/student') && to.path !== '/student/login' && !localStorage.getItem('student_token'))
    return '/student/login'
})

export default router
