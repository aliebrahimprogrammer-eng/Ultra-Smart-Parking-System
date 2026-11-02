import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react'
import * as api from './api.js'

const AuthContext = createContext(null)

function readUser(token) {
  if (!token) return null
  try {
    const encoded = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')
    const claims = JSON.parse(atob(encoded))
    if (claims.exp * 1000 <= Date.now()) return null
    return { email: claims.sub, role: claims.role, isAdmin: claims.role === 'ADMIN' }
  } catch {
    return null
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => readUser(api.getToken()))

  const logout = useCallback(() => {
    api.clearToken()
    setUser(null)
  }, [])

  const login = useCallback(async (email, password) => {
    const token = await api.login(email, password)
    api.setToken(token)
    setUser(readUser(token))
  }, [])

  const register = useCallback(
    async (details) => {
      await api.register(details)
      await login(details.email, details.password)
    },
    [login],
  )

  useEffect(() => {
    window.addEventListener(api.SESSION_EXPIRED_EVENT, logout)
    return () => window.removeEventListener(api.SESSION_EXPIRED_EVENT, logout)
  }, [logout])

  const value = useMemo(() => ({ user, login, register, logout }), [user, login, register, logout])
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export const useAuth = () => useContext(AuthContext)
