# Frontend Development Standards — React 19 + Vite 6 + TypeScript

## Project Structure

```
src/
├── components/       # Reusable UI components
│   ├── ui/           # shadcn/ui components (auto-generated)
│   ├── layout/       # Layout components (Header, Sidebar, Footer)
│   └── common/       # Shared business components
├── pages/            # Route-level page components
├── hooks/            # Custom React hooks
├── lib/              # Utility functions, API client
│   ├── api.ts        # Axios instance + request/response interceptors
│   ├── utils.ts      # General utility functions
│   └── constants.ts  # App-wide constants
├── store/            # Zustand stores
├── types/            # TypeScript type definitions
│   ├── api.ts        # API request/response types
│   └── models.ts     # Domain model types
├── router.tsx        # Route configuration
├── App.tsx           # Root component
├── main.tsx          # Entry point
└── index.css         # Tailwind imports + global styles
```

## Component Template

```tsx
import { cn } from '@/lib/utils'

interface UserCardProps {
  user: User
  onEdit?: (id: string) => void
  className?: string
}

export function UserCard({ user, onEdit, className }: UserCardProps) {
  return (
    <div className={cn('rounded-lg border p-4', className)}>
      <h3 className="text-lg font-medium">{user.name}</h3>
      <p className="text-sm text-muted-foreground">{user.email}</p>
      {onEdit && (
        <button
          onClick={() => onEdit(user.id)}
          className="mt-2 text-sm text-primary hover:underline"
        >
          Edit
        </button>
      )}
    </div>
  )
}
```

## API Client Setup (`lib/api.ts`)

```tsx
import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.response.use(
  (res) => {
    const { code, message, data } = res.data
    if (code !== 200) {
      return Promise.reject(new Error(message || 'Request failed'))
    }
    return data
  },
  (error) => {
    return Promise.reject(error)
  }
)

export default api
```

## Page with TanStack Query

```tsx
import { useQuery } from '@tanstack/react-query'
import api from '@/lib/api'
import type { User } from '@/types/models'

export function UserListPage() {
  const { data: users, isLoading, error } = useQuery({
    queryKey: ['users'],
    queryFn: () => api.get<User[]>('/users'),
  })

  if (isLoading) return <div>Loading...</div>
  if (error) return <div>Error loading users</div>

  return (
    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
      {users?.map((user) => (
        <UserCard key={user.id} user={user} />
      ))}
    </div>
  )
}
```

## Zustand Store

```tsx
import { create } from 'zustand'

interface AuthState {
  user: User | null
  token: string | null
  login: (user: User, token: string) => void
  logout: () => void
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  token: null,
  login: (user, token) => set({ user, token }),
  logout: () => set({ user: null, token: null }),
}))
```

## Router Config (`router.tsx`)

```tsx
import { createBrowserRouter } from 'react-router-dom'
import { lazy, Suspense } from 'react'

const UserListPage = lazy(() => import('@/pages/UserListPage'))
const UserDetailPage = lazy(() => import('@/pages/UserDetailPage'))

export const router = createBrowserRouter([
  {
    path: '/',
    element: <AppLayout />,
    children: [
      { index: true, element: <Suspense><UserListPage /></Suspense> },
      { path: 'users/:id', element: <Suspense><UserDetailPage /></Suspense> },
    ],
  },
])
```

## Component Testing (Vitest + Testing Library)

```tsx
import { render, screen, fireEvent } from '@testing-library/react'
import { describe, it, expect, vi } from 'vitest'
import { UserCard } from './UserCard'

describe('UserCard', () => {
  const mockUser = { id: '1', name: 'Alice', email: 'alice@test.com' }

  it('renders user name and email', () => {
    render(<UserCard user={mockUser} />)
    expect(screen.getByText('Alice')).toBeInTheDocument()
    expect(screen.getByText('alice@test.com')).toBeInTheDocument()
  })

  it('calls onEdit when edit button clicked', () => {
    const onEdit = vi.fn()
    render(<UserCard user={mockUser} onEdit={onEdit} />)
    fireEvent.click(screen.getByText('Edit'))
    expect(onEdit).toHaveBeenCalledWith('1')
  })
})
```

## Styling Rules

- Use Tailwind utility classes exclusively — no custom CSS files
- Use `cn()` from `lib/utils.ts` for conditional class merging
- shadcn/ui components for complex UI (dialogs, dropdowns, tables, forms)
- Dark mode: use `dark:` prefix; shadcn/ui CSS variables handle theme
- Responsive: mobile-first with `md:`, `lg:` breakpoints

## ESLint / Prettier

- ESLint flat config (`eslint.config.js`)
- Prettier with `prettier-plugin-tailwindcss` for class sorting
- `npm run lint` must pass with zero warnings before deployment
