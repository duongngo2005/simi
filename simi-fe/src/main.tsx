import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './styles/global.css'
import AppProviders from './app/providers.tsx'
import { RouterProvider } from 'react-router'
import { router } from './app/router.tsx'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <AppProviders>
      <RouterProvider router={router}/>
    </AppProviders>
  </StrictMode>,
)
