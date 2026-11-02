import { createContext, useCallback, useContext, useEffect, useRef, useState } from 'react'
import { LogOut, ParkingSquare } from 'lucide-react'
import { NavLink, Outlet } from 'react-router-dom'
import { useAuth } from '../auth.jsx'

const ToastContext = createContext(() => {})
export const useToast = () => useContext(ToastContext)

export default function AppShell() {
  const { user, logout } = useAuth()
  const [toast, setToast] = useState(null)
  const timer = useRef()

  const showToast = useCallback((message, tone = 'ok') => {
    clearTimeout(timer.current)
    setToast({ message, tone })
    timer.current = setTimeout(() => setToast(null), 4500)
  }, [])

  useEffect(() => () => clearTimeout(timer.current), [])

  const links = [
    { to: '/dashboard', label: 'Dashboard' },
    { to: '/reservations', label: 'Reservations' },
    user.isAdmin ? { to: '/management', label: 'Management' } : { to: '/vehicles', label: 'Vehicles' },
  ]

  return (
    <ToastContext.Provider value={showToast}>
      <div className="backdrop">
        <div className="frame">
          <header className="topbar">
            <div className="round-mark" aria-hidden="true">
              <ParkingSquare size={22} />
            </div>
            <nav className="nav-pill" aria-label="Main">
              {links.map((link) => (
                <NavLink key={link.to} to={link.to} className="nav-link">
                  {link.label}
                </NavLink>
              ))}
            </nav>
            <div className="topbar-user">
              <span className="user-email" title={user.email}>
                {user.email}
                {user.isAdmin && <span className="role-tag">Admin</span>}
              </span>
              <button className="round-button" onClick={logout} aria-label="Sign out" title="Sign out">
                <LogOut size={20} />
              </button>
            </div>
          </header>
          <main>
            <Outlet />
          </main>
        </div>
      </div>
      <div className="toast-region" aria-live="polite">
        {toast && <div className={`toast toast-${toast.tone}`}>{toast.message}</div>}
      </div>
    </ToastContext.Provider>
  )
}
