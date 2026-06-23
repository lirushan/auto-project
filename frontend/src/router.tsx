import { createBrowserRouter } from 'react-router-dom'
import { lazy, Suspense } from 'react'
import AppLayout from './components/layout/AppLayout'

const HomePage = lazy(() => import('./pages/HomePage'))

export const router = createBrowserRouter([
  {
    path: '/',
    element: <AppLayout />,
    children: [{ index: true, element: <Suspense fallback={<div className="p-8 text-center">Loading...</div>}><HomePage /></Suspense> }],
  },
])
