import { Navigate, Outlet, Route, Routes } from 'react-router-dom'
import { useAuth } from './auth.jsx'
import AppShell from './components/AppShell.jsx'
import AuthPage from './pages/AuthPage.jsx'
import Dashboard from './pages/Dashboard.jsx'
import Management from './pages/Management.jsx'
import Reservations from './pages/Reservations.jsx'
import Vehicles from './pages/Vehicles.jsx'

function RequireAuth() {
  const { user } = useAuth()
  return user ? <AppShell /> : <Navigate to="/login" replace />
}

function RequireRole({ admin }) {
  const { user } = useAuth()
  return user.isAdmin === admin ? <Outlet /> : <Navigate to="/dashboard" replace />
}

function GuestOnly({ mode }) {
  const { user } = useAuth()
  return user ? <Navigate to="/dashboard" replace /> : <AuthPage mode={mode} />
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<GuestOnly mode="login" />} />
      <Route path="/register" element={<GuestOnly mode="register" />} />
      <Route element={<RequireAuth />}>
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/reservations" element={<Reservations />} />
        <Route element={<RequireRole admin={false} />}>
          <Route path="/vehicles" element={<Vehicles />} />
        </Route>
        <Route element={<RequireRole admin={true} />}>
          <Route path="/management" element={<Management />} />
        </Route>
      </Route>
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  )
}
